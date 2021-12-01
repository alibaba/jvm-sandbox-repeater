package com.alibaba.jvm.sandbox.repeater.plugin.openfeign;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Author: luwenrong
 * @Title: OpenFeignProcessor
 * @Description:
 * @Date: 2021/11/30
 */
public class OpenFeignProcessor extends DefaultInvocationProcessor {

    OpenFeignProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        //feign.Request
        Object request = event.argumentArray[0];
        String urlStr = "get url failded";
        String methodName = "get method failded";

        try {
            urlStr = (String)MethodUtils.invokeMethod(request, "url");
            methodName = (String)MethodUtils.invokeMethod(request, "method");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LogUtil.error("openfeign plugin assembleIdentity failed", e);
        }
        return new Identity(InvokeType.OPENFEIGN.name(), methodName, urlStr, null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        //execute(Request request, Options options)
        Object request = event.argumentArray[0];
        try {
            String url = (String)MethodUtils.invokeMethod(request, "url");
            String method = (String)MethodUtils.invokeMethod(request, "method");
            Object header = MethodUtils.invokeMethod(request, "headers");
            byte[] body = (byte[]) MethodUtils.invokeMethod(request, "body");
            Object requestTemplate =  MethodUtils.invokeMethod(request, "requestTemplate");
            Object queries = MethodUtils.invokeMethod(requestTemplate, "queries");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestMethod", method);
            params.put("requestHeaders", header);
            params.put("requestParams", queries);
            params.put("requestBody", new String(body));

            return new Object[]{params};
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return super.assembleRequest(event);
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN){
            ReturnEvent returnEvent = (ReturnEvent) event;
            // feign.Response
            Object response = returnEvent.object;
            try {
                int status = (int)MethodUtils.invokeMethod(response, "status");
                String reason = (String)MethodUtils.invokeMethod(response, "reason");
                Map<String, Collection<String>> headers = (Map<String, Collection<String>>)MethodUtils.invokeMethod(reason, "headers");
                Map<String, Object> responseBody = this.getResponseBody(response, response.getClass().getClassLoader());

                Map<String, Object> responseMap = new HashMap<String, Object>();
                responseMap.put("responseStatus", status);
                responseMap.put("responseReason", reason);
                responseMap.put("responseHeaders", headers);
                if (MapUtils.isNotEmpty(responseBody)) {
                    responseMap.put("responseBodyLength", responseBody.get("length"));
                    responseMap.put("responseBodyContent", responseBody.get("content"));
                }
                return responseMap;
            }catch (Exception e){
                LogUtil.error("feign plugin save response failed",  e);
            }
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        try {
            Object request = event.argumentArray[0];
            Map<String, Object> responseMap = (Map<String, Object>) invocation.getResponse();
            if (MapUtils.isEmpty(responseMap)){
                new Object();
            }

            int status = (int)responseMap.get("responseStatus");
            String reason = (String) responseMap.get("responseReason");
            Map<String, Collection<String>> headers = (Map<String, Collection<String>>)responseMap.get("responseHeaders");
            int length = (int)responseMap.get("responseBodyLength");
            byte[] content = (byte[])responseMap.get("responseBodyContent");

            event.javaClassLoader.loadClass("feign.Response$Body");

            Class<?> responseBuilderClass = event.javaClassLoader.loadClass("feign.Response$Builder");
            Object responseBuilder = responseBuilderClass.newInstance();

            MethodUtils.invokeMethod(responseBuilder, "status", status);
            MethodUtils.invokeMethod(responseBuilder, "reason", reason);
            MethodUtils.invokeMethod(responseBuilder, "headers", headers);
            MethodUtils.invokeMethod(responseBuilder, "request", request);
            MethodUtils.invokeMethod(responseBuilder, "body", new ByteArrayInputStream(content), length);

            return MethodUtils.invokeMethod(responseBuilder, "build");
        }catch (Exception e){
            LogUtil.error("feign plugin assembleMockResponse failed, event={}", event.javaClassName + "|" + event.javaMethodName, e);
        }
        return null;
    }

    private Map<String, Object> getResponseBody(Object response, ClassLoader classLoader) throws Exception{
        Map<String, Object> responseBodyMap = new HashMap<>();

        Object body = MethodUtils.invokeMethod(response, "body");

        InputStream content = (InputStream)MethodUtils.invokeMethod(body, "asInputStream");
        int length = (int)MethodUtils.invokeMethod(body, "length");

        responseBodyMap.put("content", this.toByteArray(content));
        responseBodyMap.put("length", length);
        return responseBodyMap;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        byte[] bytes;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copyStream(in, out);
            bytes = out.toByteArray();
        } finally {
            in.close();
        }
        return bytes;
    }

    private long copyStream(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[2048];
        long total = 0L;

        while(true) {
            int r = from.read(buf);
            if (r == -1) {
                return total;
            }
            to.write(buf, 0, r);
            total += (long)r;
        }
    }

}
