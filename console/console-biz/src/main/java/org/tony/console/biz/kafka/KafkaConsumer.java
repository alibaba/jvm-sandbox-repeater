package org.tony.console.biz.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.tony.console.biz.RecordBizService;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.config.EnvConfig;

import javax.annotation.Resource;


/**
 * @author peng.hu1
 * @Date 2022/12/6 18:19
 */
@Slf4j
@Component
public class KafkaConsumer {

    @Resource
    RecordBizService recordBizService;

    @Resource
    EnvConfig envConfig;

    @KafkaListener(topics = {"${kafka.topic.record}"})
    public void listenRecordMsg(ConsumerRecord<String, String> msg) throws BizException {
        log.info("receive record msg=" + msg.key());
        recordBizService.saveRecord(msg.value());
    }


    @KafkaListener(topics = {"${kafka.topic.replay}"})
    public void listenRepeatMsg(ConsumerRecord<String, String> msg) throws BizException {
        log.info("receive replay msg=" + msg.key());
        recordBizService.saveRepeat(msg.value());
    }

}
