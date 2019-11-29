package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * {@link DubboProviderInvocationProcessor} dubbo服务端调用处理
 * <p>
 *
 * @author zhaoyb1990
 */
class DubboProviderInvocationProcessor extends DubboConsumerInvocationProcessor {

    DubboProviderInvocationProcessor(InvokeType type) {
        super(type);
    }

}
