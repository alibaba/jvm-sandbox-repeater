package com.alibaba.jvm.sandbox.repeater.plugin.rpc;

import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
/**
 * <p>
 *
 * @author qiyi-wangyeran/fanxiuping
 */
public class RpcListener extends DefaultEventListener {
    RpcListener(InvokeType invokeType,
                boolean entrance,
                InvocationListener listener,
                InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void doReturn(ReturnEvent event) {
        try {
            Invocation invocation = RecordCache.getInvocation(event.invokeId);
            if (invocation == null) {
                log.debug("no valid invocation found in return,type={},traceId={}", invokeType, Tracer.getTraceId());
                return;
            }
            invocation.setResponse(processor.assembleResponse(event));
            if (invocation.getIdentity().getLocation().startsWith("receiveBase")){
                Object[] requests =  invocation.getRequest();
                requests[0] = invocation.getResponse();
                invocation.setRequest(requests);
                invocation.getIdentity().setEndpoint(invocation.getResponse().toString());
            }
            invocation.setEnd(System.currentTimeMillis());
            listener.onInvocation(invocation);
        } catch (Exception e) {
            log.error("error occurred when doReturn:{}", e);
        }
    }
}
