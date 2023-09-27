package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:19
 */
@Data
public class AppGroovyConfigDO {

    private Long id;

    private String appName;

    private String type;

    private int status;

    private String user;

    private int version;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String content;

    private String name;

    private String env;
}
