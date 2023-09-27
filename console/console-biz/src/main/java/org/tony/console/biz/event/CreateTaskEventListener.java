package org.tony.console.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.service.model.TaskDTO;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/13 10:55
 */
@Slf4j
@Component
public class CreateTaskEventListener {

    @Resource
    TestTaskBizService testTaskBizService;

    @Async
    @EventListener(TaskDTO.class)
    public void create(TaskDTO taskDTO) {

        try {
            testTaskBizService.runTask(taskDTO);
        } catch (Exception e) {
            log.error("system error", e);
        }
    }
}
