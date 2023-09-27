package com.alibaba.jvm.sandbox.repeater.plugin.java;


import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
class JavaInvocationProcessor extends DefaultInvocationProcessor {

    JavaInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        try {
            JavaInstanceCache.cacheInstance(event.target);
        } catch (Exception e) {
            // ignore
        }
        String className = event.javaClassName;
        if (event.target!=null) {
            className = event.target.getClass().getName();
        }
        return new Identity(getType().name(), className, event.javaMethodName + "~" + event.javaMethodDesc, getExtra());
    }
}
