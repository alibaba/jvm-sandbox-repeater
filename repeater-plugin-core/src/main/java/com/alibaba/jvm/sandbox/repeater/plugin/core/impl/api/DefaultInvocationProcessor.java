package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * {@link DefaultInvocationProcessor} 默认调用处理器实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class DefaultInvocationProcessor extends AbstractInvocationProcessor {

    protected final InvokeType type;

    public DefaultInvocationProcessor(InvokeType type) {
        this.type = type;
    }

    @Override
    protected InvokeType getType() {
        return type;
    }


    @Override
    public String toString() {
        return getClass().getName()+";invokeType=" + getType() ;
    }
}
