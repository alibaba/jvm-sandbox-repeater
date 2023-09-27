package com.alibaba.jvm.sandbox.repeater.plugin.date;

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

@MetaInfServices(InvokePlugin.class)
public class DatePlugin extends AbstractInvokePluginAdapter {

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA_DATE;
    }

    @Override
    public String identity() {
        return "java-date";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel em = EnhanceModel.builder()
                .includeBootstrap(true)
                .classPattern("java.util.Date")
                .methodPatterns(new EnhanceModel.MethodPattern[]{
                        new EnhanceModel.MethodPattern("<init>", new String[]{}, null, true)
                })
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS, Event.Type.CALL_RETURN)
                .build();

        EnhanceModel em2 = EnhanceModel.builder()
                .includeBootstrap(true)
                .classPattern("java.lang.System")
                .methodPatterns(EnhanceModel.MethodPattern.transform("currentTimeMillis"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS, Event.Type.CALL_RETURN)
                .build();

        return Lists.newArrayList(em, em2);
    }

    protected EventListener getEventListener(InvocationListener listener) {
        return new DatePluginEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new DatePluginProcessor(getType());
    }

}
