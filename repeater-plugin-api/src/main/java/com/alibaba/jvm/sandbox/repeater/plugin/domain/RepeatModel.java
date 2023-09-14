package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@link RepeatModel} 回放消息数据类型
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeatModel implements java.io.Serializable {

    private String repeatId;

    private boolean finish;

    private Object response;

    private Object originResponse;

    private Object diff;

    private Long cost;

    private String traceId;

    private List<MockInvocation> mockInvocations;

    private String throwableSerialized;
    /**
     * 扩展信息
     */
    private Map<String,String> extension = new HashMap<String, String>();

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Object getOriginResponse() {
        return originResponse;
    }

    public void setOriginResponse(Object originResponse) {
        this.originResponse = originResponse;
    }

    public Object getDiff() {
        return diff;
    }

    public void setDiff(Object diff) {
        this.diff = diff;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<MockInvocation> getMockInvocations() {
        return mockInvocations;
    }

    public void setMockInvocations(List<MockInvocation> mockInvocations) {
        this.mockInvocations = mockInvocations;
    }

    public Map<String, String> getExtension() {
        if (extension==null) {
            extension = new HashMap<>();
        }
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    public String getThrowableSerialized() {
        return throwableSerialized;
    }

    public void setThrowableSerialized(String throwableSerialized) {
        this.throwableSerialized = throwableSerialized;
    }
}
