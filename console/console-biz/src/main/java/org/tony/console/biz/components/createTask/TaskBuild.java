package org.tony.console.biz.components.createTask;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tony.console.biz.Constant;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.query.TestCaseQuery;
import org.tony.console.service.TaskService;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.enums.TaskType;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 13:37
 */
@Slf4j
@Order(100)
@Component
public class TaskBuild implements CreateTaskComponent {

    @Resource
    TaskService taskService;

    @Resource
    TestCaseService testCaseService;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void execute(CreateTestTaskBizRequest request) throws BizException {

        if (request.getGmtExec() == null) {
            request.setGmtExec(new Date());
        }

        List<ModuleInfoBO> filteredList = (List<ModuleInfoBO>) BizTemplate.getSession().get(KEY_MODULE_LIST);

        TaskDTO taskDTO = build(request, filteredList);

        //计算总共多少case
        List<Long> taskIdList = request.getTestTaskIdList();

        //优先以taskId为准
        if (taskIdList!=null) {
            TestCaseQuery query = new TestCaseQuery();

            query.setAppName(request.getAppName());

            if (CollectionUtils.isNotEmpty(taskIdList)) {
                query.setSuitIdList(taskIdList);
            }

            long total = testCaseService.count(query);
            taskDTO.setTotal(Integer.valueOf(String.valueOf(total)));
            taskDTO.addExtend(Constant.TASK_LIST, taskIdList);
        }

        boolean notLazy = DateUtil.getDateGapMinNow(request.getGmtExec())>0;
        taskDTO.setLazy(!notLazy);
        try {
            Long taskId = taskService.createTask(taskDTO);
            BizTemplate.getSession().put(KEY_TASK_ID, taskId);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            //走到这里说明幂等了，查询下直接返回吧
            TaskDTO queryRes = taskService.queryByBizId(taskDTO.getAppName(), taskDTO.getBizId());
            BizTemplate.getSession().put(KEY_TASK_ID, queryRes.getId());
            return ;
        }

        //立即执行
        if (notLazy) {
            applicationContext.publishEvent(taskDTO);
        }

    }

    private TaskDTO build(CreateTestTaskBizRequest request, List<ModuleInfoBO> filteredList) {
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setAppName(request.getAppName());

        taskDTO.setName(request.getName());
        Set<String> ipSet = filteredList.stream().map(ModuleInfoBO::getIp).collect(Collectors.toSet());

        taskDTO.addExtend(Constant.IP_SET, StringUtils.join(ipSet, ","));

        if (request.getDeployTaskId()!=null) {
            taskDTO.setType(TaskType.DEPLOY);
            taskDTO.setBizId("deploy-"+request.getDeployTaskId());
        } else {
            taskDTO.setType(TaskType.TEMP_TASK_LIST);
            taskDTO.setBizId("temp-"+System.currentTimeMillis());
        }
        taskDTO.setStatus(TaskStatus.INIT);
        taskDTO.setGmtStart(request.getGmtExec());
        taskDTO.setCreator(request.getCreator());
        taskDTO.setEnv(Env.fromString(request.getEnvironment()));
        taskDTO.setDeployTaskId(request.getDeployTaskId());
        taskDTO.setDeployInstName(request.getDeployInstName());

        taskDTO.setRetryTime(request.getRetryTime());

        return taskDTO;
    }

    @Override
    public boolean isSupport(CreateTestTaskBizRequest request) {
        return true;
    }
}
