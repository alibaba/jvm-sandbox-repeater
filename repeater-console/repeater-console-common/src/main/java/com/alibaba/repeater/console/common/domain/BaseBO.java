package com.alibaba.repeater.console.common.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * {@link BaseBO}
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class BaseBO {

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
