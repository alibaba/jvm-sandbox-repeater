package org.tony.console.biz.job;

import com.nio.ndsp.core.handler.annotation.NdspJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskDTO;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/5 14:25
 */
@Slf4j
@Component
public class TaskScheduleJob {

    @Resource
    TaskService taskService;

    @Resource
    TestTaskBizService testTaskBizService;

    @NdspJob(value = "task_running_job")
    public void runningTaskJob() {
        try {
            //先查询需要初始化的任务
            List<TaskDTO> taskDTOS = queryNeedInitTaskList(TaskStatus.RUNNING);
            if (CollectionUtils.isEmpty(taskDTOS)) {
                return;
            }

            for (TaskDTO taskDTO : taskDTOS)  {

                try {
                    TaskItemQuery query = new TaskItemQuery();
                    query.setTaskId(taskDTO.getId());
                    query.setTaskStatusList(Arrays.asList(TaskStatus.RUNNING, TaskStatus.INIT, TaskStatus.FAIL_NEED_RETRY));
                    long sum = taskService.countItem(query);
                    if (sum == 0) {
                        testTaskBizService.endTask(taskDTO);
                    }

                } catch (Exception e) {
                    log.error("run task error", e);
                }

            }
        } catch (Exception e) {
            log.error("system error", e);
        }
    }


    @NdspJob(value = "task_init_job")
    public void initTaskJob() {

        try {
            //先查询需要初始化的任务
            List<TaskDTO> taskDTOS = queryNeedInitTaskList(TaskStatus.INIT);
            if (CollectionUtils.isEmpty(taskDTOS)) {
                return;
            }

            for (TaskDTO taskDTO : taskDTOS)  {
                if (!taskDTO.getLazy()) {
                    continue;
                }
                try {
                    run(taskDTO);
                } catch (Exception e) {
                    log.error("run task error", e);
                }
            }
        } catch (Exception e) {
            log.error("system error", e);
        }

    }

    private void run(TaskDTO taskDTO) throws BizException {
        testTaskBizService.runTask(taskDTO);
    }

    private List<TaskDTO> queryNeedInitTaskList(TaskStatus status) {

        TaskQuery taskQuery = new TaskQuery();
        taskQuery.setPage(1);
        taskQuery.setPageSize(10);
        taskQuery.setTaskStatus(status);
        //取当前时间
        taskQuery.setGmtStartLt(new Date());

        return taskService.query(taskQuery);
    }
}
