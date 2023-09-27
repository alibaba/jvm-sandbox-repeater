package org.tony.console.biz.model.convert;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.tony.console.biz.model.TaskVO;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.service.model.TaskDTO;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 19:42
 */
@Component
public class TaskVOConvert {

    public TaskVO convert(TaskDTO dto) {
        TaskVO taskVO = new TaskVO();

        if (dto.getStatus().equals(TaskStatus.INIT)) {
            taskVO.setDuration(0);
        } else if (dto.getStatus().equals(TaskStatus.RUNNING)) {
            Date now = new Date();
            taskVO.setDuration(now.getTime()-dto.getGmtStart().getTime());
            double rate = (dto.getSuccess()*1.0)/dto.getTotal() * 100;
            DecimalFormat format = new DecimalFormat("##.00");
            taskVO.setSuccessRate(new Double(format.format(rate)));
        } else {
            taskVO.setDuration(dto.getGmtUpdate().getTime()-dto.getGmtStart().getTime());

            if (dto.getTotal() == 0) {
                taskVO.setSuccessRate(0.0);
            } else {
                double rate = (dto.getSuccess()*1.0)/dto.getTotal() * 100;
                DecimalFormat format = new DecimalFormat("##.00");
                taskVO.setSuccessRate(new Double(format.format(rate)));
            }
        }

        taskVO.setFail(dto.getFail());
        taskVO.setAppName(dto.getAppName());
        taskVO.setGmtCreate(dto.getGmtCreate());
        taskVO.setGmtStart(dto.getGmtStart());
        taskVO.setGmtUpdate(dto.getGmtUpdate());
        taskVO.setId(dto.getId());
        taskVO.setRunning(dto.getRunning());
        taskVO.setName(dto.getName());
        taskVO.setStatus(dto.getStatus());
        taskVO.setSuccess(dto.getSuccess());
        taskVO.setTotal(dto.getTotal());
        taskVO.setType(dto.getType());
        taskVO.setEnv(dto.getEnv().name());
        taskVO.setUser(dto.getCreator());

        return taskVO;
    }

    public List<TaskVO> convert(List<TaskDTO> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>(0);
        }

        return sourceList.stream().map(this::convert).collect(Collectors.toList());
    }
}
