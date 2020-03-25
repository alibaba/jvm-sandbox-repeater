package com.alibaba.repeater.console.common.params;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link BaseParams}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class BaseParams implements java.io.Serializable {

    private Integer page = 1;

    private Integer size = 10;

    private String appName;

    private String traceId;

    private String environment;
}
