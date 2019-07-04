package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import com.alibaba.jvm.sandbox.repeater.plugin.api.FlowDispatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.RepeaterBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;

/**
 * {@link DefaultFlowDispatcher} 默认的流量分配器实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class DefaultFlowDispatcher implements FlowDispatcher {

    private DefaultFlowDispatcher() { }

    public static FlowDispatcher instance() {
        return DefaultFlowDispatcher.LazyInstanceHolder.INSTANCE;
    }

    private final static class LazyInstanceHolder {
        private final static FlowDispatcher INSTANCE = new DefaultFlowDispatcher();
    }

    @Override
    public void dispatch(RepeatMeta meta, RecordModel recordModel) throws RepeatException {
        if (recordModel == null || recordModel.getEntranceInvocation() == null || recordModel.getEntranceInvocation().getType() == null) {
            throw new RepeatException("invalid request, record or root invocation is null");
        }
        Repeater repeater = RepeaterBridge.instance().select(recordModel.getEntranceInvocation().getType());
        if (repeater == null) {
            throw new RepeatException("no valid repeat found for invoke type:" + recordModel.getEntranceInvocation().getType());
        }
        RepeatContext context = new RepeatContext(meta, recordModel, TraceGenerator.generate());
        // 放置到回放缓存中
        RepeatCache.putRepeatContext(context);
        repeater.repeat(context);
    }
}
