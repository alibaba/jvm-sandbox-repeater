package com.alibaba.jvm.sandbox.repeater.plugin.ibatis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
class IBatisProcessor extends DefaultInvocationProcessor {

    IBatisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }
}
