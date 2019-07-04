package com.alibaba.jvm.sandbox.repeater.plugin.domain;



import java.util.Map;

/**
 * {@link HttpInvocation} http请求调用比较独立
 * <p>
 *
 * @author zhaoyb1990
 */
public class HttpInvocation extends Invocation implements java.io.Serializable {

    /**
     * 请求的URL
     */
    private String requestURL;
    /**
     * 请求的URI
     */
    private String requestURI;
    /**
     * 本地端口号
     */
    private int port;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 内容类型
     */
    private String contentType;
    /**
     * 请求headers
     */
    private Map<String, String> headers;
    /**
     * 请求参数 - 没有拦截post body方式提交;没有同步序列化，导致被更改，只能使用request里面的参数
     */
    private Map<String, String[]> paramsMap;

    /**
     * POST请求的BODY
     */
    private String body;

    /**
     * 异步调用
     */
    private volatile boolean async;

    /**
     * 是否已初始化
     */
    private transient boolean init;

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String[]> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, String[]> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
