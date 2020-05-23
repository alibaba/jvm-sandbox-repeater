package com.alibaba.jvm.sandbox.repeater.plugin.java;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.kohsuke.MetaInfServices;

/**
 * java子调用插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class JavaSubInvokePlugin extends AbstractInvokePluginAdapter {

    private volatile RepeaterConfig config;

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        if (config == null || CollectionUtils.isEmpty(config.getJavaSubInvokeBehaviors())) {
            return null;
        }
        List<EnhanceModel> ems = Lists.newArrayList();
        for (Behavior behavior : config.getJavaSubInvokeBehaviors()) {
            ems.add(EnhanceModel.convert(behavior));
        }
        return ems;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new JavaInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA;
    }

    @Override
    public String identity() {
        return "java-subInvoke";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        this.config = config;
        return super.enable(config);
    }

    @Override
    public void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException {
        if (configTemporary == null) {
            super.onConfigChange(config);
        } else {
            List<Behavior> current = config.getJavaSubInvokeBehaviors();
            List<Behavior> latest = configTemporary.getJavaSubInvokeBehaviors();
            this.config = config;
            super.onConfigChange(config);
            if (JavaPluginUtils.hasDifference(current, latest)) {
                log.error("onConfigChange,config={},configTemporary={}", config, configTemporary);
                reWatch0();
            }
        }
    }
}
