package com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.MethodSignatureParser;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.HttpInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;


/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class RecordWrapper {

    private long timestamp;

    private String appName;

    private String environment;

    private String host;

    private String traceId;
    /**
     * 入口描述
     */
    private String entranceDesc;
    /**
     * 入口调用
     */
    private Invocation entranceInvocation;
    /**
     * 子调用信息
     */
    private List<Invocation> subInvocations;

    public RecordWrapper() {}

    public RecordWrapper(RecordModel recordModel) {
        this.timestamp = recordModel.getTimestamp();
        this.appName = recordModel.getAppName();
        this.environment = recordModel.getEnvironment();
        this.host = recordModel.getHost();
        this.traceId = recordModel.getTraceId();
        if (recordModel.getEntranceInvocation() instanceof HttpInvocation) {
            this.entranceDesc = ((HttpInvocation)recordModel.getEntranceInvocation()).getRequestURL();
        } else {
            this.entranceDesc = recordModel.getEntranceInvocation().getIdentity().getUri();
        }
        this.entranceInvocation = recordModel.getEntranceInvocation();
        this.subInvocations = recordModel.getSubInvocations();
    }

    /**
     * 将{@link RecordWrapper} 转换成 {@link RecordModel}
     * 
     * @return Record
     */
    public RecordModel reTransform() {
        RecordModel recordModel = new RecordModel();
        recordModel.setTimestamp(this.timestamp);
        recordModel.setTraceId(this.traceId);
        recordModel.setAppName(this.appName);
        recordModel.setEnvironment(this.environment);
        recordModel.setHost(this.host);
        recordModel.setEntranceInvocation(this.entranceInvocation);
        recordModel.setSubInvocations(this.subInvocations);
        return recordModel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getEntranceDesc() {
        return entranceDesc;
    }

    public void setEntranceDesc(String entranceDesc) {
        this.entranceDesc = entranceDesc;
    }

    public Invocation getEntranceInvocation() {
        return entranceInvocation;
    }

    public void setEntranceInvocation(Invocation entranceInvocation) {
        this.entranceInvocation = entranceInvocation;
    }

    public List<Invocation> getSubInvocations() {
        return subInvocations;
    }

    public void setSubInvocations(List<Invocation> subInvocations) {
        this.subInvocations = subInvocations;
    }
}
