package com.alibaba.jvm.sandbox.repeater.plugin.diff;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author peng.hu1
 * @Date 2023/1/10 18:12
 */
public class BaseDO {

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
