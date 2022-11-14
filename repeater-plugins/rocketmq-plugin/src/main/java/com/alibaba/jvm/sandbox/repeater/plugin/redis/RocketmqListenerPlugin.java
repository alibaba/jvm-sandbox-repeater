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

public class RocketmqListenerPlugin extends AbstractInvokePluginAdapter {
    @Override
    public InvokeType getType() {
        return InvokeType.ROCKETMQ;
    }

    @Override
    public String identity() {
        return "rocketmq-consumer";
    }

    @Override
    public boolean isEntrance() {
        // 消息消費時，會作為調用鏈路的入口
        return true;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel messageListenerConcurrently = EnhanceModel.builder().classPattern("org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently")
                .methodPatterns(EnhanceModel.MethodPattern.transform("consumeMessage"))
                .includeSubClasses(true)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel messageListenerOrderly = EnhanceModel.builder().classPattern("org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly")
                .includeSubClasses(true)
                .methodPatterns(EnhanceModel.MethodPattern.transform("consumeMessage"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(messageListenerConcurrently, messageListenerOrderly);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RocketmqListenerInvocationProcessor(getType());
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new RocketmqListenerEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
