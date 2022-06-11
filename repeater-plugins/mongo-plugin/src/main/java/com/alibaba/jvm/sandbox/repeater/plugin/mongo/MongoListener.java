package com.alibaba.jvm.sandbox.repeater.plugin.mongo;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.mongo.wrapper.MongoWrapperTransModel;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@link MongoListener} 继承 {@link DefaultEventListener}但是由于http有同步异步两种策略，因此需要重写一些方法
 * <p>
 *
 * @author wangyeran
 */
public class MongoListener extends DefaultEventListener  {
    MongoListener(InvokeType invokeType,
                  boolean entrance,
                  InvocationListener listener,
                  InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void doBefore(BeforeEvent event) {
        log.debug("inner the before");
        Invocation invocation = new Invocation();
        invocation.setStart(System.currentTimeMillis());
        invocation.setTraceId(Tracer.getTraceId());
        invocation.setIndex(entrance ? 0 : SequenceGenerator.generate(Tracer.getTraceId()));
        invocation.setIdentity(processor.assembleIdentity(event));
        invocation.setEntrance(entrance);
        invocation.setType(invokeType);
        invocation.setProcessId(event.processId);
        invocation.setInvokeId(event.invokeId);
        invocation.setRequest(processor.assembleRequest(event));
        invocation.setResponse(processor.assembleResponse(event));
        invocation.setSerializeToken(ClassloaderBridge.instance().encode(event.javaClassLoader));
        try {
            SerializerWrapper.inTimeSerialize(invocation);
        } catch (SerializeException e) {
            Tracer.getContext().setSampled(false);
            log.error("Error occurred serialize", e);
        }
        RecordCache.cacheInvocation(event.invokeId, invocation);
    }

    /**
     * 处理return事件
     *
     * @param event return事件
     */
    protected void doReturn(ReturnEvent event) {
        Invocation invocation = RecordCache.getInvocation(event.invokeId);
        if (invocation == null) {
            log.info("no invocation");
            log.debug("no valid invocation found in return,type={},traceId={}", invokeType, Tracer.getTraceId());
            return;
        }
        log.info("response here 1");
        invocation.setResponse(processor.assembleResponse(event));
        invocation.setEnd(System.currentTimeMillis());
        listener.onInvocation(invocation);
    }

    @Override
    protected void doThrow(ThrowsEvent event) {
        Invocation invocation = RecordCache.getInvocation(event.invokeId);
        if (invocation == null) {
            log.debug("no valid invocation found in throw,type={},traceId={}", invokeType, Tracer.getTraceId());
            return;
        }
        invocation.setThrowable(processor.assembleThrowable(event));
        invocation.setEnd(System.currentTimeMillis());
        listener.onInvocation(invocation);
    }
}
