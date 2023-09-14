package com.alibaba.jvm.sandbox.repeater.plugin.date;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;

import java.util.Date;

public class DatePluginEventListener extends DefaultEventListener {

    public DatePluginEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    public void onEvent(Event event) throws Throwable {
        if (!event.type.equals(Event.Type.BEFORE)) {
            return;
        }

        BeforeEvent e = (BeforeEvent) event;



        //只处理回放流量
        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {

            //processor.doMock(event, entrance, invokeType);
            RepeatContext repeatContext = RepeatCache.getRepeatContext(Tracer.getTraceId());
            if (repeatContext == null) {
                return;
            }

            if (!repeatContext.getCanMockDate()) {
                return;
            }

            //获取录制时间
            long recordTime = repeatContext.getRecordModel().getTimestamp();

            if (e.javaClassName.equals("java.util.Date")) {

                Date now  = (Date) e.target;
                now.setTime(recordTime);

                return;
            }

            if (e.javaClassName.equals("java.lang.System")) {
                ProcessControlException.throwReturnImmediately(recordTime);
            }
        }
    }
}
