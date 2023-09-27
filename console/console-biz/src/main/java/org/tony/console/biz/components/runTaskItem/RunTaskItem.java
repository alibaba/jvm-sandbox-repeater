package org.tony.console.biz.components.runTaskItem;

import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tony.console.biz.Constant;
import org.tony.console.biz.ReplayBizService;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.ReplayRequest;
import org.tony.console.biz.request.RunTaskItemRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskItemDTO;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/6 12:12
 */
@Slf4j
@Order(20)
@Component
public class RunTaskItem implements RunTaskItemComponent {

    @Resource
    ReplayBizService replayBizService;

    @Resource
    TaskService taskService;

    @Override
    public void execute(RunTaskItemRequest runTaskItemRequest) throws BizException {

        List<ModuleInfoBO> moduleInfoBOList = runTaskItemRequest.getAvailableModuleList();

        TaskItemDTO taskItemDTO = runTaskItemRequest.getTaskItem();
        String repeateId = TraceGenerator.generate();
        ModuleInfoBO m = moduleInfoBOList.get(0);

        taskItemDTO.addExtend(Constant.REPEAT_IP, m.getIp());
        taskItemDTO.addExtend(Constant.REPEAT_ID, repeateId);

        int flag = taskService.runTaskItem(taskItemDTO.getTaskId(), taskItemDTO);
        if (flag!=1) {
            log.error("task update status error, id={}, version={}", taskItemDTO.getId(), taskItemDTO.getVersion());
            return;
        }

        ReplayRequest r = new ReplayRequest();
        r.setAppName(runTaskItemRequest.getTaskDTO().getAppName());
        r.setModuleInfoBO(m);
        r.setCaseId(taskItemDTO.getName());
        r.setTaskItemId(taskItemDTO.getId());
        r.setRepeatId(repeateId);
        r.setSingle(false);
        try {
            Result resp = replayBizService.replayV2(r);
            if (!resp.isSuccess()) {
                taskService.failTaskItem(taskItemDTO.getTaskId(),taskItemDTO);
            }

        } catch (Exception e) {
            log.error("system error", e);
        }

    }

    @Override
    public boolean isSupport(RunTaskItemRequest request) {
        if (CollectionUtils.isNotEmpty(request.getAvailableModuleList())) {
            return true;
        }
        return false;
    }
}
