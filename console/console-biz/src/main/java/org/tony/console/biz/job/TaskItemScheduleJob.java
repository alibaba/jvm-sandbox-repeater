package org.tony.console.biz.job;

import com.nio.ndsp.core.handler.annotation.NdspJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskItemDTO;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author peng.hu1
 * @Date 2023/1/6 10:37
 */
@Slf4j
@Component
public class TaskItemScheduleJob {

    @Resource
    TaskService taskService;

    @Resource
    TestTaskBizService testTaskBizService;

    @Value("${task.item.run.batch.size}")
    private Integer batchSize = 100;

    @NdspJob(value = "task_item_run")
    public void runTaskItem() {
        try {

            List<TaskItemDTO> taskItemDTOList = queryTaskItem();
            if (CollectionUtils.isEmpty(taskItemDTOList)) {
                return;
            }

            List<TaskItemDTO> needRetry = new LinkedList<>();

            List<TaskItemDTO> needFail = new LinkedList<>();

            List<TaskItemDTO> needRun = new LinkedList<>();

            for (TaskItemDTO taskItemDTO : taskItemDTOList) {

                if (TaskStatus.RUNNING.equals(taskItemDTO.getStatus())) {
                    long gap = DateUtil.getDateGapS(taskItemDTO.getGmtUpdate(), new Date());

                    //超过30s，就重试
                    if (gap>30) {
                        needRetry.add(taskItemDTO);
                    }
                }

                if (TaskStatus.FAIL_NEED_RETRY.equals(taskItemDTO.getStatus())) {
                    if (taskItemDTO.getExecTime()>=taskItemDTO.getRetryTime()+1) {
                        needFail.add(taskItemDTO);
                    } else {
                        needRun.add(taskItemDTO);
                    }
                }
            }

            for (TaskItemDTO itemDTO : needRetry) {
                testTaskBizService.retryTaskItem(itemDTO);
            }

            for (TaskItemDTO itemDTO : needRun) {
                testTaskBizService.runTaskItem(itemDTO.getTaskId(), itemDTO);
            }

            for (TaskItemDTO itemDTO : needFail) {
                testTaskBizService.failTaskItem(itemDTO.getTaskId(), itemDTO);
            }

        } catch (Exception e) {
            log.error("system error", e);
        }
    }

    private List<TaskItemDTO> queryTaskItem() {
        TaskItemQuery query = new TaskItemQuery();
        query.setTaskStatusList(Arrays.asList(TaskStatus.RUNNING, TaskStatus.FAIL_NEED_RETRY));
        query.setPage(1);
        query.setPageSize(batchSize);
        return taskService.queryItem(query);
    }
}
