package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

import static com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator.getSampleBit;
import static com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator.isValid;
import static java.lang.Long.parseLong;

/**
 * {@link TraceContext} 定义一个简单的上下文，用于串联一次完成调用
 *
 * <p>
 *
 * @author zhaoyb1990
 */
public class TraceContext {

    /**
     * 唯一标识一次调用
     */
    private String traceId;

    /**
     * 调用发生时间
     */
    private long timestamp;

    /**
     * 被采样到 - 采样规则由入口调用计算，子调用不计算
     */
    private volatile boolean sampled;

    /**
     * 调用的类型{@link com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType}
     */
    private InvokeType invokeType;

    /**
     * 额外需要透传的信息可以用这个承载
     */
    private Map<String, String> extra = new HashMap<String, String>();

    TraceContext(String traceId) {
        this.timestamp = System.currentTimeMillis();
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getExtra(String key) {
        return extra.get(key);
    }

    public String putExtra(String key, String value) {
        return extra.put(key, value);
    }

    public boolean isSampled() {
        return sampled;
    }

    public void setSampled(boolean sampled) {
        this.sampled = sampled;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
    }

    /**
     * 及时计算采样
     * @param invokeType 调用类型
     * @return 是否被采样
     */
    public boolean inTimeSample(InvokeType invokeType) {
        // 第一级入口流量才会计算采样；非自身入口类型直接抛弃
        if (this.invokeType == null || this.invokeType.equals(invokeType)) {
            boolean sampled = isValid(traceId) && parseLong(getSampleBit(traceId)) % 10000 < ApplicationModel.instance().getSampleRate();
            this.invokeType = invokeType;
            this.sampled = sampled;
            return sampled;
        } else {
            // 下级入口不采集
            return false;
        }
    }
}
