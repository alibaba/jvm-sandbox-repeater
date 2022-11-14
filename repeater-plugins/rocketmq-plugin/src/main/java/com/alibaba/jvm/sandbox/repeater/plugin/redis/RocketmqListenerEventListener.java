package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RocketInvocation;
import org.apache.commons.lang3.reflect.MethodUtils;

public class RocketmqListenerEventListener extends DefaultEventListener {

    public RocketmqListenerEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected Invocation initInvocation(BeforeEvent beforeEvent) {
        RocketInvocation rocketInvocation = new RocketInvocation();
        try {
            Object mqArg = beforeEvent.argumentArray[0];
            Object msgExt = MethodUtils.invokeMethod(mqArg, "get", 0);
            String topic = String.valueOf(MethodUtils.invokeMethod(msgExt, "getTopic"));
            String tags = String.valueOf(MethodUtils.invokeMethod(msgExt, "getTags"));
            String msgId = String.valueOf(MethodUtils.invokeMethod(msgExt, "getMsgId"));
            String body = new String((byte[]) MethodUtils.invokeMethod(msgExt, "getBody"));
            rocketInvocation.setMessageTopic(topic);
            rocketInvocation.setMessageTags(tags);
            rocketInvocation.setMessageId(msgId);
            rocketInvocation.setMessageBody(body);
        } catch (Exception exception) {
            log.error("init listener rocketInvocation error", exception);
        }
        return rocketInvocation;
    }
}
