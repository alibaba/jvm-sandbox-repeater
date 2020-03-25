package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.IBuildingForBehavior;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.IBuildingForClass;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AbstractInvokePluginAdapter}是{@link InvokePlugin}的抽象适配，提供了标准的模块生命周期处理流程；
 * <p>
 * 同时注入了{@link com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationListener}
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractInvokePluginAdapter implements InvokePlugin {

    protected final static Logger log = LoggerFactory.getLogger(AbstractInvokePluginAdapter.class);

    protected volatile RepeaterConfig configTemporary;

    private ModuleEventWatcher watcher;

    private List<Integer> watchIds = Lists.newCopyOnWriteArrayList();

    private InvocationListener listener;

    private AtomicBoolean watched = new AtomicBoolean(false);

    @Override
    public void onLoaded() throws PluginLifeCycleException {
        // default no-op
    }

    @Override
    public void onActive() throws PluginLifeCycleException {
        // default no-op
    }

    @Override
    public void watch(ModuleEventWatcher watcher,
                      InvocationListener listener) throws PluginLifeCycleException {
        this.watcher = watcher;
        this.listener = listener;
        watchIfNecessary();
    }

    @Override
    public void unWatch(ModuleEventWatcher watcher, InvocationListener listener) {
        if (CollectionUtils.isNotEmpty(watchIds)) {
            for (Integer watchId : watchIds) {
                watcher.delete(watchId);
            }
            watchIds.clear();
        }
        watched.compareAndSet(true, false);
    }

    @Override
    public void reWatch(ModuleEventWatcher watcher,
                        InvocationListener listener) throws PluginLifeCycleException {
        this.unWatch(watcher, listener);
        watch(watcher, listener);
    }

    @Override
    public void onFrozen() throws PluginLifeCycleException {
        // default no-op
    }

    @Override
    public void onUnloaded() throws PluginLifeCycleException {
        // default no-op
    }

    @Override
    public void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException {
        // default no-op;plugin can override this method to aware config change
        this.configTemporary = config;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return config != null && config.getPluginIdentities().contains(identity());
    }

    protected void reWatch0() throws PluginLifeCycleException {
        reWatch(watcher, listener);
    }

    /**
     * 执行观察事件
     *
     * @throws PluginLifeCycleException 插件异常
     */
    private synchronized void watchIfNecessary() throws PluginLifeCycleException {
        if (watched.compareAndSet(false, true)) {
            List<EnhanceModel> enhanceModels = getEnhanceModels();
            if (CollectionUtils.isEmpty(enhanceModels)) {
                throw new PluginLifeCycleException("enhance models is empty, plugin type is " + identity());
            }
            for (EnhanceModel em : enhanceModels) {
                IBuildingForBehavior behavior = null;
                IBuildingForClass builder4Class = new EventWatchBuilder(watcher).onClass(em.getClassPattern());
                if (em.isIncludeSubClasses()) {
                    builder4Class = builder4Class.includeSubClasses();
                }
                for (EnhanceModel.MethodPattern mp : em.getMethodPatterns()) {
                    behavior = builder4Class.onBehavior(mp.getMethodName());
                    if (ArrayUtils.isNotEmpty(mp.getParameterType())) {
                        behavior.withParameterTypes(mp.getParameterType());
                    }
                    if (ArrayUtils.isNotEmpty(mp.getAnnotationTypes())) {
                        behavior.hasAnnotationTypes(mp.getAnnotationTypes());
                    }
                }
                if (behavior != null) {
                    int watchId = behavior.onWatch(getEventListener(listener), em.getWatchTypes()).getWatchId();
                    watchIds.add(watchId);
                    log.info("add watcher success,type={},watcherId={}", getType().name(), watchId);
                }
            }
        }
    }

    /**
     * 获取需要增强的类模型
     *
     * @return enhanceModels
     */
    abstract protected List<EnhanceModel> getEnhanceModels();

    /**
     * 返回调用过程处理器，用于处理入参、返回值等
     *
     * @return invocationProcessor构造结果
     */
    abstract protected InvocationProcessor getInvocationProcessor();

    /**
     * 返回事件监听器 - 子类若参数的组装方式不适配，可以重写改方法
     *
     * @param listener 调用监听
     * @return 事件监听器
     */
    protected EventListener getEventListener(InvocationListener listener) {
        return new DefaultEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
