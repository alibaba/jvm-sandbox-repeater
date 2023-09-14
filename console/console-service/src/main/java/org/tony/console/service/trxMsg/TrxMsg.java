package org.tony.console.service.trxMsg;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/3/27 09:40
 */
@Data
public class TrxMsg<T> {

    private Long id;

    private Topic topic;

    private T content;

    private int execTimes;

    private Date gmtExec;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Group group;

    private MsgStatus msgStatus;
}
