package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/1/4 10:54
 */
@Data
public class TaskDO {

    private Long id;

    private String appName;

    private String name;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Date gmtStart;

    private Integer status;

    private Integer type;

    private String extend;

    private int version;

    private String creator;

    private String env;

    /**
     * 幂等字段
     */
    private String bizId;
}
