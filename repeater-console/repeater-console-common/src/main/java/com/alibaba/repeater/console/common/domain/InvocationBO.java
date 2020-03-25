package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link InvocationBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class InvocationBO extends BaseBO {

    private String identity;

    private String invokeType;

    private Integer index;

    private Integer processId;

    private Integer invokeId;

    private Object[] request;

    private Object response;

    private Throwable throwable;

    private Long cost;

    @Override
    public String toString() {
        return super.toString();
    }
}
