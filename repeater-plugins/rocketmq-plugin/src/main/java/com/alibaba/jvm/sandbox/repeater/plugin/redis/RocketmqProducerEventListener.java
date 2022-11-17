package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RocketInvocation;
import org.apache.commons.lang3.reflect.MethodUtils;

public class RocketmqProducerEventListener extends DefaultEventListener {

    public RocketmqProducerEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected Invocation initInvocation(BeforeEvent beforeEvent) {
        RocketInvocation rocketInvocation = new RocketInvocation();
        Object mqArg = beforeEvent.argumentArray[0];
        try {
            String topic = String.valueOf(MethodUtils.invokeMethod(mqArg, "getTopic"));
            String tags = String.valueOf(MethodUtils.invokeMethod(mqArg, "getTags"));
            String body = new String((byte[]) MethodUtils.invokeMethod(mqArg, "getBody"));
            rocketInvocation.setMessageTopic(topic);
            rocketInvocation.setMessageTags(tags);
            rocketInvocation.setMessageBody(body);
        } catch (Exception exception) {
            log.error("init producer rocketInvocation error", exception);
        }
        return rocketInvocation;
    }
}
