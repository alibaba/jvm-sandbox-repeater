package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;

/**
 * {@link InvokePlugin} 核心接口，定义一个调用插件
 * <p>
 * 每个类型的流量都需要实现该插件完成录制
 * </p>
 *
 * @author zhaoyb1990
 */
public interface InvokePlugin {

    /**
     * 调用类型
     *
     * @return 调用类型
     * @see com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType
     */
    InvokeType getType();

    /**
     * 身份标识 - 唯一标识一个插件
     * 因为同一个 {@link InvokeType} 会存在入口调用和子调用插件，{@link InvokePlugin#getType} 不能唯一标识一个插件
     *
     * @return identity 能够唯一标志插件
     */
    String identity();

    /**
     * 是否是入口流量插件
     *
     * @return true/false
     */
    boolean isEntrance();

    /**
     * 是否生效
     *
     * @param config 配置文件
     * @return true/false
     */
    boolean enable(RepeaterConfig config);

    /**
     * 被加载之前
     *
     * @throws PluginLifeCycleException 插件周期异常
     */
    void onLoaded() throws PluginLifeCycleException;

    /**
     * 被激活
     *
     * @throws PluginLifeCycleException 插件周期异常
     */
    void onActive() throws PluginLifeCycleException;

    /**
     * 重新初始化 (例如:推送配置之后，需要重新增强代码)
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     * @throws PluginLifeCycleException 插件周期异常
     */
    void watch(ModuleEventWatcher watcher,
               InvocationListener listener) throws PluginLifeCycleException;

    /**
     * 删除插件
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     */
    void unWatch(ModuleEventWatcher watcher,
                 InvocationListener listener);

    /**
     * 重新初始化 (例如:推送配置之后，需要重新增强代码)
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     * @throws PluginLifeCycleException 插件周期异常
     */
    void reWatch(ModuleEventWatcher watcher,
                 InvocationListener listener) throws PluginLifeCycleException;

    /**
     * 被冻结
     *
     * @throws PluginLifeCycleException 插件周期异常
     */
    void onFrozen() throws PluginLifeCycleException;

    /**
     * 被卸载
     *
     * @throws PluginLifeCycleException 插件周期异常
     */
    void onUnloaded() throws PluginLifeCycleException;

    /**
     * 监听配置变化
     *
     * @param config 配置文件
     * @throws PluginLifeCycleException 插件周期异常
     */
    void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException;

}
