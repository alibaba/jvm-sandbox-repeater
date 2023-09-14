package org.tony.console.service.convert;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.tony.console.common.enums.TaskStatus;
import org.tony.console.db.model.TaskItemDO;

import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.service.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 15:55
 */
@Component
public class TaskItemConvert implements ModelConverter<TaskItemDO, TaskItemDTO> {

    private static final String KEY_RETRY_TIME = "retry";

    @Override
    public TaskItemDTO convert(TaskItemDO source) {
        if (source == null) {
            return null;
        }

        TaskItemDTO dto = new TaskItemDTO();

        dto.setTaskId(source.getTaskId());
        dto.setStatus(TaskStatus.getByCode(source.getStatus()));
        dto.setType(TaskType.getByCode(source.getType()));
        dto.setName(source.getName());
        dto.setVersion(source.getVersion());
        dto.setId(source.getId());
        dto.setGmtCreate(source.getGmtCreate());
        dto.setGmtUpdate(source.getGmtUpdate());
        dto.setExecTime(source.getExecTime());
        if (StringUtils.isNotEmpty(source.getExtend())) {
            dto.setExtend(JSONObject.parseObject(source.getExtend()));
        }

        if (dto.getExtend().containsKey(KEY_RETRY_TIME)) {
            dto.setRetryTime(dto.getExtend().getInteger(KEY_RETRY_TIME));
        }
        return dto;
    }

    @Override
    public List<TaskItemDTO> convert(List<TaskItemDO> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>(0);
        }

        return sourceList.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TaskItemDO> reconvertList(List<TaskItemDTO> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList<>(0);
        }

        return sList.stream().map(this::reconvert).collect(Collectors.toList());
    }

    @Override
    public TaskItemDO reconvert(TaskItemDTO target) {
        TaskItemDO taskItemDO = new TaskItemDO();
        taskItemDO.setTaskId(target.getTaskId());

        target.addExtend(KEY_RETRY_TIME, target.getRetryTime());

        if (target.getExtend()!=null) {
            taskItemDO.setExtend(target.getExtend().toJSONString());
        }

        taskItemDO.setExecTime(0);
        taskItemDO.setType(target.getType().code);
        taskItemDO.setId(target.getId());
        taskItemDO.setStatus(target.getStatus().code);
        taskItemDO.setVersion(target.getVersion());
        taskItemDO.setName(target.getName());
        taskItemDO.setExecTime(target.getExecTime());

        return taskItemDO;
    }
}
