package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * {@link DubboProviderPlugin} Apache dubbo provider
 * <p>
 * 拦截ContextFilter$ContextListener#onResponse进行录制
 * </p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class DubboProviderPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel onResponse = EnhanceModel.builder().classPattern("org.apache.dubbo.rpc.filter.ContextFilter$ContextListener")
                .methodPatterns(EnhanceModel.MethodPattern.transform("onResponse"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel invoke = EnhanceModel.builder().classPattern("org.apache.dubbo.rpc.filter.ContextFilter")
                .methodPatterns(EnhanceModel.MethodPattern.transform("invoke"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(invoke, onResponse);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new DubboProviderInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.DUBBO;
    }

    @Override
    public String identity() {
        return "dubbo-provider";
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new DubboEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
