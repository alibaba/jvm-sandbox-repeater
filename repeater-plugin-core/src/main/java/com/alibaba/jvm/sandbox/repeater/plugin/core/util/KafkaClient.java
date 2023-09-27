package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.kafka.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author peng.hu1
 * @Date 2022/12/6 17:44
 */
public class KafkaClient {

    KafkaProducer<String, String> producer;

    private KafkaConfig kafkaConfig;

    public KafkaClient(KafkaConfig kafkaConfig) {

        this.kafkaConfig = kafkaConfig;

        Properties properties = new Properties();
        //指定链接的kafka集群
        properties.put("bootstrap.servers",kafkaConfig.getServer());

        //重试次数
        properties.put("retries",2);
        //批次大小
        properties.put("batch.size",16384);//16k
        //等待时间
        properties.put("linger.ms",100);
        //RecordAccumulator缓冲区大小
        properties.put("buffer.memory",33554432);//32m
        //Key,Value的序列化类
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        String saslConfigFormat = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String saslConfig = String.format(saslConfigFormat, kafkaConfig.getUsername(), kafkaConfig.getPassword());
        properties.put("sasl.jaas.config", saslConfig);

        properties.setProperty("security.protocol", "SASL_PLAINTEXT");
        properties.setProperty("sasl.mechanism", "PLAIN");

        //创建生产者对象
        producer = new KafkaProducer<String, String>(properties);
    }

    public void sendRecordMsg(String key, String msg) {
        producer.send(
                new ProducerRecord<String, String>(kafkaConfig.getRecordTopic(), key, msg)
        );
    }

    public void sendRepeatMsg(String key, String msg) {
        producer.send(
                new ProducerRecord<String, String>(kafkaConfig.getRepeatTopic(), key, msg)
        );
    }
}
