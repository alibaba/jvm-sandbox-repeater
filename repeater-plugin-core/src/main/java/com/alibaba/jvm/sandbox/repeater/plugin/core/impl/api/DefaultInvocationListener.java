package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DefaultInvocationListener} 默认的调用监听实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class DefaultInvocationListener implements InvocationListener {

    private final static Logger log = LoggerFactory.getLogger(DefaultInvocationListener.class);

    private final Broadcaster broadcast;

    public DefaultInvocationListener(Broadcaster broadcast) {
        this.broadcast = broadcast;
    }

    @Override
    public void onInvocation(Invocation invocation) {
        try {
            SerializerWrapper.inTimeSerialize(invocation);
        } catch (SerializeException e) {
            Tracer.getContext().setSampled(false);
            log.error("Error occurred serialize", e);
        }
        if (invocation.isEntrance()) {
            ApplicationModel am = ApplicationModel.instance();
            RecordModel recordModel = new RecordModel();
            recordModel.setAppName(am.getAppName());
            recordModel.setEnvironment(am.getEnvironment());
            recordModel.setHost(am.getHost());
            recordModel.setTraceId(invocation.getTraceId());
            recordModel.setTimestamp(invocation.getStart());
            recordModel.setEntranceInvocation(invocation);
            recordModel.setSubInvocations(RecordCache.getSubInvocation(invocation.getTraceId()));
            if (log.isDebugEnabled()){
                log.debug("sampleOnRecord:traceId={},rootType={},subTypes={}", recordModel.getTraceId(), invocation.getType(), assembleTypes(recordModel));
            }
            broadcast.sendRecord(recordModel);
        } else {
            RecordCache.cacheSubInvocation(invocation);
        }
    }

    private String assembleTypes(RecordModel recordModel) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(recordModel.getSubInvocations())) {
            Map<InvokeType, AtomicInteger> counter = new HashMap<InvokeType, AtomicInteger>(1);
            for (Invocation invocation : recordModel.getSubInvocations()) {
                if (counter.containsKey(invocation.getType())) {
                    counter.get(invocation.getType()).incrementAndGet();
                } else {
                    counter.put(invocation.getType(), new AtomicInteger(1));
                }
            }
            for (Map.Entry<InvokeType, AtomicInteger> entry : counter.entrySet()) {
                builder.append(entry.getKey().name()).append("=").append(entry.getValue()).append(";");
            }
        }
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "nil";
    }
}
