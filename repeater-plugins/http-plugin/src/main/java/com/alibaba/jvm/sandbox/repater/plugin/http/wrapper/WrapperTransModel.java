package com.alibaba.jvm.sandbox.repater.plugin.http.wrapper;

import org.apache.commons.collections4.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class WrapperTransModel {

    public WrapperRequest request;

    public WrapperResponseCopier copier;

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
     * body
     */
    private String body;
    /**
     * 请求参数 - 没有拦截post body方式提交
     */
    private Map<String, String[]> paramsMap;
    /**
     * 返回值
     */
    private String response;

    private WrapperTransModel(String requestURL, String requestURI, int port, String method, String contentType,
                              Map<String, String> headers, Map<String, String[]> paramsMap) {
        this.requestURL = requestURL;
        this.requestURI = requestURI;
        this.port = port;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.paramsMap = paramsMap;
    }

    public static WrapperTransModel build(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<String, String>(2);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String[]> parameterMapHolder = new HashMap<String, String[]>(2);
        if (MapUtils.isNotEmpty(parameterMap)) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                parameterMapHolder.put(entry.getKey(), entry.getValue());
            }
        }
        return new WrapperTransModel(
                request.getRequestURL().toString(),
                request.getRequestURI(),
                request.getLocalPort(),
                request.getMethod(),
                request.getContentType(),
                headers,
                parameterMapHolder
        );
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String[]> getParamsMap() {
        return paramsMap;
    }

    public int getPort() {
        return port;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
