package org.tony.console.service.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.service.model.enums.TaskType;

import java.util.Date;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/1/4 13:22
 */
@Data
public class TaskItemDTO {

    private Long id;

    private String name;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Long taskId;

    private TaskType type;

    private TaskStatus status;

    private JSONObject extend;

    private int execTime;

    private int version;

    private int retryTime;

    /**
     * 失败说明
     */
    private String failMsg;

    public void addExtend(String k, Object v) {
        if (extend == null) {
            extend = new JSONObject();
        }

        extend.put(k, v);
    }
}
