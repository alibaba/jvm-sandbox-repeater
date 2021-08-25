package com.alibaba.jvm.sandbox.repater.plugin.rocketmq;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * @author fengLiang
 */
public class RocketMqProcessor extends DefaultInvocationProcessor {
    public RocketMqProcessor(InvokeType type) {
        super(type);
    }
    
    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        // DefaultMQProducerImpl#sendDefaultImpl(Message msg,final CommunicationMode communicationMode,final SendCallback sendCallback,final long timeout)
        // 只序列化前两个参数
        return new Object[]{event.argumentArray[0], event.argumentArray[1]};
    }

}
