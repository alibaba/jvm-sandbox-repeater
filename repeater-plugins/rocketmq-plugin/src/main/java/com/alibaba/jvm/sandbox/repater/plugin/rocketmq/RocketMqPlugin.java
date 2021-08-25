package com.alibaba.jvm.sandbox.repater.plugin.rocketmq;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * @author fengLiang
 */
@MetaInfServices(InvokePlugin.class)
public class RocketMqPlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder().classPattern("org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl")
            .methodPatterns(EnhanceModel.MethodPattern.transform("sendDefaultImpl"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RocketMqProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.ROCKET_MQ;
    }

    @Override
    public String identity() {
        return "rocket_mq";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
