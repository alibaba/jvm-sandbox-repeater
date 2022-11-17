package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

public class RocketmqListenerInvocationProcessor extends DefaultInvocationProcessor {
    public RocketmqListenerInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (event.argumentArray != null && event.argumentArray.length > 0) {
            return new Object[]{event.argumentArray[0]};
        }
        return event.argumentArray;
    }
}
