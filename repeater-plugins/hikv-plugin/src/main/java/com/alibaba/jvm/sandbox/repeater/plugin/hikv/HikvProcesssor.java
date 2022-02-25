package com.alibaba.jvm.sandbox.repeater.plugin.hikv;

import java.util.HashMap;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * <p>
 *
 * @author wangyeran
 */
class HikvProcesssor extends DefaultInvocationProcessor {

    HikvProcesssor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        return new Identity(InvokeType.HIKV.name(), event.argumentArray[0].toString(), "Unknown", new HashMap<String, String>(1));
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        return new Object[]{event.argumentArray[0]};
    }
}
