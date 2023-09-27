package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/14 09:25
 */
@Data
public class TestSuitDO {

    private Long id;

    private String name;

    private String appName;

    private Long parentId;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Integer type;

    private String extend;

    private Integer status;
}
