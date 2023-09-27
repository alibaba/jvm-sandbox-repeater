package org.tony.console.biz.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskItemDTO;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/13 10:07
 */
@Slf4j
@Component
public class TaskKafkaConsumer {

    @Resource
    TaskService taskService;

    @Resource
    TestTaskBizService testTaskBizService;

    @KafkaListener(topics = {"${kafka.task.topic}"}, containerFactory = "kafkaOneContainerFactory")
    public void listenerOne(ConsumerRecord<String, String> record) throws BizException {
        String idString = record.value();

        log.info("run task item id={}", idString);

        TaskItemDTO taskItemDTO = taskService.queryItemById(Long.valueOf(idString));

        try {
            testTaskBizService.runTaskItem(taskItemDTO.getTaskId(), taskItemDTO);
        } catch (Exception e) {
            log.error("system error", e);
            throw e;
        }
    }
}
