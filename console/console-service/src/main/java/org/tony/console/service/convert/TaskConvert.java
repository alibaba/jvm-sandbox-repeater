package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.model.TaskDO;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/4 13:47
 */
@Component
public class TaskConvert implements ModelConverter<TaskDO, TaskDTO> {

    private final static String KEY_TOTAL = "total";

    private final static String KEY_SUCCESS = "success";

    private final static String KEY_RUNNING = "running";

    private final static String KEY_LAZY = "lazy";

    private final static String KEY_FAIL = "fail";

    private final static String KEY_DEPLOY_ID = "deploy";

    private final static String KEY_DEPLOY_INST = "dInst";

    private final static String KEY_RETRY_TIME = "retry";

    @Override
    public TaskDTO convert(TaskDO source) {
        if (source == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setStatus(TaskStatus.getByCode(source.getStatus()));
        taskDTO.setType(TaskType.getByCode(source.getType()));
        taskDTO.setCreator(source.getCreator());
        taskDTO.setName(source.getName());
        taskDTO.setId(source.getId());
        taskDTO.setGmtUpdate(source.getGmtUpdate());
        taskDTO.setGmtCreate(source.getGmtCreate());
        taskDTO.setAppName(source.getAppName());
        taskDTO.setVersion(source.getVersion());
        taskDTO.setGmtStart(source.getGmtStart());
        taskDTO.setEnv(Env.fromString(source.getEnv()));
        taskDTO.setBizId(source.getBizId());

        taskDTO.setTotal(0);
        taskDTO.setSuccess(0);
        taskDTO.setFail(0);
        taskDTO.setRunning(0);

        if (StringUtils.isNotEmpty(source.getExtend())) {
            taskDTO.setExtend(JSON.parseObject(source.getExtend()));

            JSONObject extend = taskDTO.getExtend();
            taskDTO.setTotal(extend.getInteger(KEY_TOTAL));
            taskDTO.setSuccess(extend.getInteger(KEY_SUCCESS));
            taskDTO.setFail(extend.getInteger(KEY_FAIL));
            taskDTO.setRunning(extend.getInteger(KEY_RUNNING));

            if (extend.containsKey(KEY_DEPLOY_ID)) {
                taskDTO.setDeployTaskId(extend.getString(KEY_DEPLOY_ID));
            }

            if (extend.containsKey(KEY_DEPLOY_INST)) {
                taskDTO.setDeployInstName(extend.getString(KEY_DEPLOY_INST));
            }

            if (extend.containsKey(KEY_LAZY)) {
                taskDTO.setLazy(true);
            } else {
                taskDTO.setLazy(false);
            }

            if (extend.containsKey(KEY_RETRY_TIME)) {
                taskDTO.setRetryTime(extend.getInteger(KEY_RETRY_TIME));
            }
        }

        return taskDTO;
    }

    @Override
    public List<TaskDTO> convert(List<TaskDO> taskDOS) {
        if (CollectionUtils.isEmpty(taskDOS)) {
            return new ArrayList<>(0);
        }
        return taskDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TaskDO> reconvertList(List<TaskDTO> sList) {
        return null;
    }

    @Override
    public TaskDO reconvert(TaskDTO target) {
        if (target == null) {
            return null;
        }

        TaskDO taskDO = new TaskDO();
        taskDO.setId(target.getId());
        taskDO.setAppName(target.getAppName());
        taskDO.setVersion(target.getVersion());
        taskDO.setType(target.getType().code);
        taskDO.setGmtStart(target.getGmtStart());
        taskDO.setStatus(target.getStatus().code);
        taskDO.setName(target.getName());
        taskDO.setCreator(target.getCreator());
        taskDO.setEnv(target.getEnv().name());
        taskDO.setBizId(target.getBizId());

        if ( target.getExtend() == null ) {
            target.setExtend(new JSONObject());
        }

        if (target.getTotal()!=null) {
            target.getExtend().put(KEY_TOTAL, target.getTotal());
        }

        if (target.getSuccess()!=null) {
            target.getExtend().put(KEY_SUCCESS, target.getSuccess());
        }

        if (target.getFail()!=null) {
            target.getExtend().put(KEY_FAIL, target.getFail());
        }

        if (target.getRunning()!=null) {
            target.getExtend().put(KEY_RUNNING, target.getRunning());
        }

        if (target.getLazy()!=null && target.getLazy()) {
            target.getExtend().put(KEY_LAZY, 1);
        }

        if (target.getDeployTaskId()!=null) {
            target.getExtend().put(KEY_DEPLOY_ID, target.getDeployTaskId());
        }

        if (target.getDeployInstName()!=null) {
            target.getExtend().put(KEY_DEPLOY_INST, target.getDeployInstName());
        }

        if (target.getRetryTime()!=null) {
            target.getExtend().put(KEY_RETRY_TIME, target.getRetryTime());
        }

        //这个一定要放到最后
        taskDO.setExtend(target.getExtend().toString());
        return taskDO;
    }
}
