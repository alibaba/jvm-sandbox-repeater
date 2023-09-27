package com.alibaba.jvm.sandbox.repeater.plugin.date;


import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;

import static com.alibaba.jvm.sandbox.api.event.Event.Type.BEFORE;

public class DatePluginProcessor extends DefaultInvocationProcessor {

    public DatePluginProcessor(InvokeType type) {
        super(type);
    }


    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        if (!event.type.equals(BEFORE)) {
            return true;
        }

        return false;
    }
}
