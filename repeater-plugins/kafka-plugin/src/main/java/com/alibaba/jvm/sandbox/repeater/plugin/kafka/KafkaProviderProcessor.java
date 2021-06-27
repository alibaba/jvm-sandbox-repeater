package com.alibaba.jvm.sandbox.repeater.plugin.kafka;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * {@link KafkaProviderProcessor} kafka-producer子插件
 *
 * @author quansong
 * @version 1.0
 */
class KafkaProviderProcessor extends DefaultInvocationProcessor {

    KafkaProviderProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        // KafkaProducer#doSend(ProducerRecord<K, V> record, Callback callback)
        // callback存在不可序序列化异常
        return new Object[]{event.argumentArray[0]};
    }

}
