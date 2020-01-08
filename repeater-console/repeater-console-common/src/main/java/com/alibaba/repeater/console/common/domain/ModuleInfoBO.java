package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * {@link ModuleInfoBO}
 * <p>
 * 在线模块信息
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class ModuleInfoBO extends BaseBO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String appName;

    private String environment;

    private String ip;

    private String port;

    private String version;

    private ModuleStatus status;

    @Override
    public String toString() {
        return super.toString();
    }
}
