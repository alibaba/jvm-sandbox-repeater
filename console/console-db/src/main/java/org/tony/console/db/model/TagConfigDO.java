package org.tony.console.db.model;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:19
 */
@Data
public class TagConfigDO {

    private Long id;

    private String appName;

    private String name;

    private String nick;

    private int scope;

    private String identity;

    private String jsonpath;
}
