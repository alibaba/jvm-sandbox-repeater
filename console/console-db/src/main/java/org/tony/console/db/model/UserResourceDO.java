package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/2/10 08:58
 */
@Data
public class UserResourceDO {

    private Long id;

    private String user;

    private Long rid;

    private String name;

    private int role;

    private int type;

    private int status;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String extend;
}
