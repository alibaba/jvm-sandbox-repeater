package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/1 20:40
 */
@Data
public class AppConfig {

    private Long id;

    private String appName;

    private String config;

    private int type;

    private Date gmtCreate;

    private String env;
}
