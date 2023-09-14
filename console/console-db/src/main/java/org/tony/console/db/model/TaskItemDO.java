package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/1/4 11:08
 */
@Data
public class TaskItemDO {

    private Long id;

    private String name;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Long taskId;

    private Integer type;

    private Integer status;

    private String extend;

    /**
     * 执行次数
     */
    private int execTime;

    private int version;
}
