package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link DifferenceBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class DifferenceBO extends BaseBO {

    private String actual;
    private String expect;
    private String type;
    private String nodeName;

    @Override
    public String toString() {
        return super.toString();
    }
}
