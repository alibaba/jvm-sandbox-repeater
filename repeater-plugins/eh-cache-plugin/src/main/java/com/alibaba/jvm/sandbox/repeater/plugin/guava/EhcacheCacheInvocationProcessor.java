package com.alibaba.jvm.sandbox.repeater.plugin.guava;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * EhcacheCacheInvocationProcessor - guava cache处理插件
 *
 * @author vivo-刘延江
 * @version 1.0
 * @CreateDate: 2020/11/24 15:58
 */
public class EhcacheCacheInvocationProcessor extends DefaultInvocationProcessor {
    public EhcacheCacheInvocationProcessor(InvokeType type) {
        super(type);
    }
}
