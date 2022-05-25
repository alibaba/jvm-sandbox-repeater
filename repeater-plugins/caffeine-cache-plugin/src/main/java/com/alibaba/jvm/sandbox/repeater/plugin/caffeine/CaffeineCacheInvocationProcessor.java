package com.alibaba.jvm.sandbox.repeater.plugin.caffeine;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * CaffeineCacheInvocationProcessor - guava cache处理插件
 *
 * @author vivo-钱兆良
 * @version 1.0
 * @CreateDate: 2020/11/5 17:38
 */
public class CaffeineCacheInvocationProcessor extends DefaultInvocationProcessor {
    public CaffeineCacheInvocationProcessor(InvokeType type) {
        super(type);
    }
}
