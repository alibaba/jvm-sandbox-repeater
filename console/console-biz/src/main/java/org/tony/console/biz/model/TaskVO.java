package org.tony.console.biz.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.service.model.enums.TaskType;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/1/5 19:37
 */
@Data
public class TaskVO {

    private Long id;

    private String name;

    private String appName;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Date gmtStart;

    private TaskStatus status;

    private TaskType type;

    /**
     * 总个数
     */
    private Integer total;

    /**
     * 成功个数
     */
    private Integer success;

    /**
     * 失败个数
     */
    private Integer fail;

    private Integer running;

    /**
     * 耗时
     */
    private long duration;

    /**
     * 成功率
     */
    private Double successRate;

    /**
     * 环境
     */
    private String env;

    /**
     * 操作人
     */
    private String user;
}
