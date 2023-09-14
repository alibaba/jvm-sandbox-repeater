package org.tony.console.db.model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleInfo implements Serializable {

    private static final long serialVersionUID = 1668489227510L;


    /**
    * 主键
    * 主键
    * isNullAble:0
    */
    private Long id;

    /**
    * 创建时间
    * isNullAble:0
    */
    private Date gmtCreate;

    /**
    * 修改时间
    * isNullAble:0
    */
    private Date gmtModified;

    /**
    * 应用名
    * isNullAble:0
    */
    private String appName;

    /**
    * 环境信息
    * isNullAble:0
    */
    private String environment;

    /**
    * 机器IP
    * isNullAble:0
    */
    private String ip;

    /**
    * 链路追踪ID
    * isNullAble:0
    */
    private String port;

    /**
    * 模块版本号
    * isNullAble:0
    */
    private String version;

    /**
    * 模块状态
    * isNullAble:0
    */
    private String status;
}
