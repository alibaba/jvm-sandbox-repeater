package com.alibaba.jvm.sandbox.repeater.plugin.kafka;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;

import org.kohsuke.MetaInfServices;
import java.util.*;

/**
 * {@link KafkaProviderPlugin} kafka-producer子插件
 *
 * @author quansong
 * @version 1.0
 */
@MetaInfServices(InvokePlugin.class)
public class KafkaProviderPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel redisson = EnhanceModel.builder()
                .classPattern("org.apache.kafka.clients.producer.KafkaProducer")
                .methodPatterns(EnhanceModel.MethodPattern.transform("doSend"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(redisson);
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new KafkaProviderProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.KAFKA;
    }

    @Override
    public String identity() {
        return "kafka-provider";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }


}
