package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * {@link Invocation} 描述一次调用
 *
 * <p>
 * {@link Invocation} 调用信息抽象；一次完整的{@link RecordModel} 由一次入口{@link RecordModel#entranceInvocation}和若干次子{@link RecordModel#subInvocations}组装
 *
 * {@link Invocation} 主要记录了调用时的入参、返回值、调用顺序等信息，用于回放流量的发起和子调用的Mock还原
 * </p>
 *
 * @author zhaoyb1990
 */
public class Invocation implements java.io.Serializable {

    /**
     * 调用类型
     *
     * @see com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType
     */
    private InvokeType type;

    /**
     * @see com.alibaba.jvm.sandbox.api.event.InvokeEvent#invokeId
     */
    private int invokeId;

    /**
     * @see com.alibaba.jvm.sandbox.api.event.InvokeEvent#processId
     */
    private int processId;

    /**
     * 链路跟踪ID（仅应用内部，对于分布式系统需要自己重新定义）
     */
    private String traceId;

    /**
     * 调用的身份识别
     *
     * @see com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity
     */
    private Identity identity;

    /**
     * 调用发生在应用内部的sequence序号
     */
    private Integer index;

    /**
     * 是否是入口调用类型（类似 HTTP/DUBBO)
     */
    private boolean entrance;

    /**
     * 请求参数 - snapshot 不做传输使用，传输需要序列化值
     *
     * @see Invocation#requestSerialized
     */
    private transient Object[] request;

    /**
     * 序列化之后的请求值，录制时候作为{@link Invocation#request}的载体传输；回放时候需要还原成{@link Invocation#request}
     */
    private String requestSerialized;

    /**
     * 返回结果 - snapshot 不做传输使用
     */
    private transient Object response;

    /**
     * 序列化之后的请求值，录制时候作为{@link Invocation#response}的载体传输；回放时候需要还原成{@link Invocation#response}
     */
    private String responseSerialized;

    /**
     * 异常信息 - snapshot 不做传输使用
     */
    private transient Throwable throwable;

    /**
     * 序列化之后的请求值，录制时候作为{@link Invocation#throwable}的载体传输；回放时候需要还原成{@link Invocation#throwable}
     */
    private String throwableSerialized;

    /**
     * 调用开始时间
     */
    private Long start;

    /**
     * 调用结束时间
     */
    private Long end;

    /**
     * 序列化token
     */
    private String serializeToken;

    /**
     * 目标调用的类加载（透传不做传输）
     */
    private transient ClassLoader classLoader;

    public InvokeType getType() {
        return type;
    }

    public void setType(InvokeType type) {
        this.type = type;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public boolean isEntrance() {
        return entrance;
    }

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public Object[] getRequest() {
        return request;
    }

    public void setRequest(Object[] request) {
        this.request = request;
    }

    public String getRequestSerialized() {
        return requestSerialized;
    }

    public void setRequestSerialized(String requestSerialized) {
        this.requestSerialized = requestSerialized;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getResponseSerialized() {
        return responseSerialized;
    }

    public void setResponseSerialized(String responseSerialized) {
        this.responseSerialized = responseSerialized;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getThrowableSerialized() {
        return throwableSerialized;
    }

    public void setThrowableSerialized(String throwableSerialized) {
        this.throwableSerialized = throwableSerialized;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getSerializeToken() {
        return serializeToken;
    }

    public void setSerializeToken(String serializeToken) {
        this.serializeToken = serializeToken;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}