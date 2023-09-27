package com.alibaba.jvm.sandbox.repeater.plugin.okhttp;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.okhttp.util.HttpOkUtil;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * okhttp的调用处理器
 *
 * @Author: vivo-孙道明
 * @version 1.0
 * @CreateDate: 2020/11/1 21:15
 */
public class OkhttpInvocationProcessor extends DefaultInvocationProcessor {

    private static final Integer PEEK_BODY_SIZE = Integer.MAX_VALUE;

    private static Joiner joiner =Joiner.on('/').skipNulls();

    OkhttpInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {

        String urlStr = "okhttp-plugin get uri error";
        // 真实对应的类okhttp3.Request
        Object request = this.getRequestFromEvent(event);
        try {
            Object url =MethodUtils.invokeMethod(request, "url");
            urlStr = HttpUtil.getPureUrL(MethodUtils.invokeMethod(url, "scheme"),
                    MethodUtils.invokeMethod(url, "host"));
            Collection<String>collection = (Collection<String>) MethodUtils.invokeMethod(url, "pathSegments");
            if(collection !=null && collection.size() >0) {
                urlStr = urlStr + "/" +joiner.join(collection);
            }

        } catch (Exception e) {
            LogUtil.error("okhttp-plugin assembleIdentity get url error", e);
        }

        return new Identity(InvokeType.OKHTTP.name(), urlStr, "", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {

        try {
            // 真实对应的类okhttp3.Request
            Object request = this.getRequestFromEvent(event);
            Object url = MethodUtils.invokeMethod(request, "url");
            Object method = MethodUtils.invokeMethod(request, "method");
            //Object header = MethodUtils.invokeMethod(request, "headers");
            Object paramsStr = MethodUtils.invokeMethod(url, "query");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestMethod", String.valueOf(method));
            // request的header可以简单处理，就用string即可
            // params.put("requestHeaders", String.valueOf(header));
            params.put("requestParams", HttpUtil.getParamMap(String.valueOf(paramsStr)));
            params.put("requestBody", HttpOkUtil.getBody(request));

            return new Object[]{params};
        }catch (Exception e){
            LogUtil.error("okhttp-plugin assembleRequest error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return new Object[]{};
    }

    private Object getRequestFromEvent(BeforeEvent event) {

        try {
            // Response intercept(Chain chain)
            Object realChain = event.argumentArray[0];
            return MethodUtils.invokeMethod(realChain, "request");
        }catch (Exception e){
            LogUtil.error("okhttp-plugin getRequestFromEvent error", e);
        }

        return new Object();
    }

    @Override
    public Object assembleResponse(Event event) {
        // assembleResponse可能在before事件中被调用，这里只需要在return时间中获取返回值
        if (event.type == Event.Type.RETURN){
            ReturnEvent returnEvent = (ReturnEvent) event;
            // 获取返回值
            Map<String, Object> responseMap = new HashMap<String, Object>();
            Object response = returnEvent.object;
            if (response == null){
                return responseMap;
            }

            try {
                // 拷贝一份返回值
                Object peekBody = MethodUtils.invokeMethod(response, "peekBody", PEEK_BODY_SIZE);
                String responseBody = MethodUtils.invokeMethod(peekBody, "string").toString();

                Object protocol = MethodUtils.invokeMethod(response, "protocol");
                Object code = MethodUtils.invokeMethod(response, "code");
                Object message = MethodUtils.invokeMethod(response, "message");
                // 这里需要将header细化，以便后续好构造mock返回结果
                Map<String, List<String>> headers = this.getResponseHeadersMap(response);

                responseMap.put("responseHeaders", headers);
                responseMap.put("responseProtocol", String.valueOf(protocol));
                responseMap.put("responseCode", code);
                responseMap.put("responseMessage", String.valueOf(message));
                responseMap.put("responseBody", responseBody);


                return responseMap;
            } catch (Exception e) {
                LogUtil.error("okhttp-plugin copy response error",  e);
            }
        }

            return null;
    }

    //
    private Map<String, List<String>> getResponseHeadersMap(Object response) throws Exception {

        Object headers =  MethodUtils.invokeMethod(response, "headers");
        if (headers == null){
            new HashMap<String, List<String>>();
        }

        return (Map<String, List<String>>)MethodUtils.invokeMethod(headers, "toMultimap");
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {

        // okhttp3.RealCall
        try {
            Object request = this.getRequestFromEvent(event);
            // 构建返回body体
            Map<String, Object> responseMap = (Map<String, Object>) invocation.getResponse();
            if (MapUtils.isEmpty(responseMap)){
                new Object();
            }

            String responseStr = (String)responseMap.get("responseBody");
            long responseContentLength = responseStr.getBytes().length;
            Class<?> bufferClass = event.javaClassLoader.loadClass("okio.Buffer");
            Object buffer = bufferClass.newInstance();

            InputStream inputStream = HttpOkUtil.getStringInputStream(responseStr);
            if (inputStream == null){
                LogUtil.error("okhttp-plugin get response stream error");
                return new Object();
            }
            MethodUtils.invokeMethod(buffer, "readFrom", inputStream);
            Class<?> realResponseBodyClass = event.javaClassLoader.loadClass("okhttp3.internal.http.RealResponseBody");
            Constructor<?>[] constructors = realResponseBodyClass.getConstructors();
            int paramsCount = constructors[0].getParameterCount();
            Object responseBody = null;

            responseBody = constructors[0].newInstance("", responseContentLength, buffer);

            Class<?> protocolClass = event.javaClassLoader.loadClass("okhttp3.Protocol");

            // 构建返回协议
            Object protocol = MethodUtils.invokeStaticMethod(protocolClass, "get", responseMap.get("responseProtocol"));

             Map<String, List<String>> responseHeaders = (Map<String, List<String>>)responseMap.get("responseHeaders");

            // 内部类需要$隔离开
            Class<?> responseBuilderClass = event.javaClassLoader.loadClass("okhttp3.Response$Builder");
            Object responseBuilder = responseBuilderClass.newInstance();

            // 构建返回对象response
            long currentTime = System.currentTimeMillis();
            MethodUtils.invokeMethod(responseBuilder, "request", request);
            MethodUtils.invokeMethod(responseBuilder, "protocol", protocol);
            MethodUtils.invokeMethod(responseBuilder, "code", (Integer) responseMap.get("responseCode"));
            MethodUtils.invokeMethod(responseBuilder, "message",  responseMap.get("responseMessage"));
            MethodUtils.invokeMethod(responseBuilder, "body", responseBody);
            MethodUtils.invokeMethod(responseBuilder, "sentRequestAtMillis", currentTime-1);
            MethodUtils.invokeMethod(responseBuilder, "receivedResponseAtMillis", currentTime);
            this.mockResponseAddHeaders(responseBuilder, responseHeaders);

            return MethodUtils.invokeMethod(responseBuilder, "build");
        }catch (Exception e){
            LogUtil.error("okhttp-plugin assembleMockResponse error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return null;
    }

    private void mockResponseAddHeaders(Object responseBuilder, Map<String, List<String>> responseHeaders) throws Exception {

        if (MapUtils.isEmpty(responseHeaders)){
            return;
        }

        for (Map.Entry<String, List<String>> headerEntry : responseHeaders.entrySet()) {

            String name = headerEntry.getKey();
            List<String> valueList = headerEntry.getValue();
            for (String value : valueList) {
                MethodUtils.invokeMethod(responseBuilder, "addHeader", name, value);
            }
        }
    }
}
