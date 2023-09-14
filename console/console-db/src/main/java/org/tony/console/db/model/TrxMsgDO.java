package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/3/27 10:14
 */
@Data
public class TrxMsgDO {

    private Long id;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String topic;

    private Date gmtExec;

    private int execTime;

    private String content;

    private String group;

    private int status;
}
