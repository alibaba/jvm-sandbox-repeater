package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.google.common.collect.Lists;

import java.util.List;

public class RocketmqProducerPlugin extends AbstractInvokePluginAdapter {
    @Override
    public InvokeType getType() {
        return InvokeType.ROCKETMQ;
    }

    @Override
    public String identity() {
        return "rocketmq-producer";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel producer = EnhanceModel.builder().classPattern("org.apache.rocketmq.client.producer.DefaultMQProducer")
                .methodPatterns(EnhanceModel.MethodPattern.transform("send"))
                .includeSubClasses(true)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(producer);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RocketmqProducerInvocationProcessor(getType());
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new RocketmqProducerEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
