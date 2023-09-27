package org.tony.console.db.query;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.enums.TaskStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 14:30
 */
@Data
public class TaskItemQuery extends BaseQuery {

    TaskStatus taskStatus;

    List<TaskStatus> taskStatusList;

    List<String> nameList;

    Long taskId;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (taskStatus!=null) {
            params.put("status", taskStatus.code);
        }

        if (taskId!=null) {
            params.put("taskId", taskId);
        }

        if (!CollectionUtils.isEmpty(taskStatusList)) {
            List<Integer> statusList = taskStatusList.stream().map(item->{
                return item.code;
            }).collect(Collectors.toList());

            params.put("statusList", statusList);
        }

        if (!CollectionUtils.isEmpty(nameList)) {
            params.put("nameList", nameList);
        }
    }
}
