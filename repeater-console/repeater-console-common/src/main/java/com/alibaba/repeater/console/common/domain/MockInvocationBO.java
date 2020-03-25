package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link MockInvocationBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class MockInvocationBO extends BaseBO {
    private Integer index;
    private Boolean success;
    private Boolean skip;
    private Long cost;
    private String originUri;
    private String currentUri;
    private String originArgs;
    private String currentArgs;

    @Override
    public String toString() {
        return super.toString();
    }
}
