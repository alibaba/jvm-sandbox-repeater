package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.MethodUtils;

public class DubboProviderEventListener extends DubboConsumerEventListener {

    private static final String ON_RESPONSE = "onResponse";

    public DubboProviderEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void initContext(Event event) {
        if (entrance && isEntranceBegin(event)) {
            Object invocation = ((BeforeEvent) event).argumentArray[1];

            Object traceId = null;
            try {
                traceId = MethodUtils.invokeMethod(invocation, "getAttachment", Constants.HEADER_TRACE_ID);
            } catch (Exception e) {
                LogUtil.warn("get invocation attachment exception.", e);
            }

            if (traceId == null) {
                Tracer.start();
            } else {
                Tracer.start(String.valueOf(traceId));
            }
        }
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            return;
        }

        super.doBefore(event);
    }

    @Override
    protected boolean isEntranceBegin(Event event) {
        if (event.type != Event.Type.BEFORE) {
            return false;
        }

        String methodName = ((BeforeEvent) event).javaMethodName;
        if (ON_RESPONSE.equals(methodName)) {
            //标记已经调用过ContextFilter$ContextListener.onResponse的BeforeEvent
            Tracer.putExtra(ON_RESPONSE, ON_RESPONSE);
            return false;
        }

        return true;
    }

    @Override
    protected boolean isEntranceFinish(Event event) {
        if (event.type == Event.Type.BEFORE || Tracer.getContext().getInvokeType() != invokeType) {
            return false;
        }

        //已经调用过ContextFilter$ContextListener.onResponse的BeforeEvent
        return Tracer.getExtra(ON_RESPONSE) != null;
    }
}
