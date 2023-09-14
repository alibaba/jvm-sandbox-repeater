package com.alibaba.jvm.sandbox.repeater.plugin.domain;


import com.alibaba.jvm.sandbox.repeater.plugin.diff.DifferenceDO;

import java.util.List;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class MockInvocation implements java.io.Serializable {
    private int index;
    private String traceId;
    private String repeatId;
    private boolean success;
    private boolean skip;
    private long cost;
    private String originUri;
    private String currentUri;
    private Object[] originArgs;
    private Object[] currentArgs;
    private int originIndex;
    private List<DifferenceDO> diffs;

    /**
     * 序列化之后的请求值
     */
    private String currentRequestSerialized;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getOriginUri() {
        return originUri;
    }

    public void setOriginUri(String originUri) {
        this.originUri = originUri;
    }

    public String getCurrentUri() {
        return currentUri;
    }

    public void setCurrentUri(String currentUri) {
        this.currentUri = currentUri;
    }

    public Object[] getOriginArgs() {
        return originArgs;
    }

    public void setOriginArgs(Object[] originArgs) {
        this.originArgs = originArgs;
    }

    public Object[] getCurrentArgs() {
        return currentArgs;
    }

    public void setCurrentArgs(Object[] currentArgs) {
        this.currentArgs = currentArgs;
    }

    public int getOriginIndex() {
        return originIndex;
    }

    public void setOriginIndex(int originIndex) {
        this.originIndex = originIndex;
    }

    public List<DifferenceDO> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<DifferenceDO> diffs) {
        this.diffs = diffs;
    }

    public String getCurrentRequestSerialized() {
        return currentRequestSerialized;
    }

    public void setCurrentRequestSerialized(String currentRequestSerialized) {
        this.currentRequestSerialized = currentRequestSerialized;
    }
}
