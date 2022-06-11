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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
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
            /**
             * 兼容老版本没有 requestTemplate
             */
            if (hasField(request.getClass(), "requestTemplate")) {
                Object requestTemplateObject = MethodUtils.invokeMethod(request, "requestTemplate");
                urlStr = (String)MethodUtils.invokeMethod(requestTemplateObject, "url");
            } else {
                String requestUrlStr = (String)MethodUtils.invokeMethod(request, "url");
                urlStr = this.getUrl(requestUrlStr);
            }
            methodName = (String)MethodUtils.invokeMethod(request, "method");
        } catch (Exception e) {
            LogUtil.error("openfeign plugin assembleIdentity failed", e);
        }
        return new Identity(InvokeType.OPENFEIGN.name(), methodName, urlStr, null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        //execute(Request request, Options options)
        Object request = event.argumentArray[0];
        try {
            String method = (String)MethodUtils.invokeMethod(request, "method");
            Object header = MethodUtils.invokeMethod(request, "headers");
            Object body =  MethodUtils.invokeMethod(request, "body");

            String urlStr = "";
            if (hasField(request.getClass(), "requestTemplate")) {
                Object requestTemplateObject = MethodUtils.invokeMethod(request, "requestTemplate");
                urlStr = (String)MethodUtils.invokeMethod(requestTemplateObject, "url");
            } else {
                String requestUrlStr = (String)MethodUtils.invokeMethod(request, "url");
                urlStr = this.getUrl(requestUrlStr);
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestUrl", urlStr);
            params.put("requestMethod", method);
            params.put("requestHeaders", header);
            params.put("requestBody", String.valueOf(body));

            return new Object[]{params};
        } catch (Exception e) {
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
                int status = Integer.parseInt(String.valueOf(MethodUtils.invokeMethod(response, "status")));
                String reason = (String)MethodUtils.invokeMethod(response, "reason");
                Map<String, Collection<String>> headers = (Map<String, Collection<String>>)MethodUtils.invokeMethod(response, "headers");
                Map<String, Object> responseBody = this.getResponseBody(response);

                Object body = MethodUtils.invokeMethod(response, true, "body");
                Field inputStreamField = FieldUtils.getDeclaredField(body.getClass(), "inputStream", true);
//                inputStreamField.setAccessible(true);
                inputStreamField.set(body, new ByteArrayInputStream((byte[]) responseBody.get("content")));

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

            int status = Integer.parseInt(String.valueOf(responseMap.get("responseStatus")));
            String reason = (String) responseMap.get("responseReason");
            Map<String, Collection<String>> headers = (Map<String, Collection<String>>)responseMap.get("responseHeaders");
            Object length = responseMap.get("responseBodyLength");
            byte[] content = (byte[])responseMap.get("responseBodyContent");

            Class<?> responseBuilderClass = event.javaClassLoader.loadClass("feign.Response$Builder");
            Constructor constructor = responseBuilderClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object responseBuilder = constructor.newInstance();

            MethodUtils.invokeMethod(responseBuilder, true, "status", status);
            MethodUtils.invokeMethod(responseBuilder, true, "reason", reason);
            MethodUtils.invokeMethod(responseBuilder, true, "headers", headers);
            MethodUtils.invokeMethod(responseBuilder, true, "request", request);
            MethodUtils.invokeMethod(responseBuilder, true, "body", new ByteArrayInputStream(content), length);

            return MethodUtils.invokeMethod(responseBuilder, true, "build");
        }catch (Exception e){
            LogUtil.error("feign plugin assembleMockResponse failed, event={}", event.javaClassName + "|" + event.javaMethodName, e);
        }
        return null;
    }

    private Map<String, Object> getResponseBody(Object response) throws Exception{
        Map<String, Object> responseBodyMap = new HashMap<String, Object>();

        Object body = MethodUtils.invokeMethod(response, true, "body");
        InputStream content = (InputStream)MethodUtils.invokeMethod(body, true, "asInputStream");
        Object lengthObject = MethodUtils.invokeMethod(body, true, "length");

        byte[] contentBuf = new byte[]{};
        if(content != null) {
            contentBuf = this.toByteArray(content);
        }

        responseBodyMap.put("content", contentBuf);
        responseBodyMap.put("length", lengthObject);
        return responseBodyMap;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        byte[] bytes;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copyStream(in, out);
            bytes = out.toByteArray();
        } finally {
            try {
                out.close();
            }catch (Exception e) {

            }
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

    private boolean hasField(Class c, String fieldName){
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            if (fieldName.equals(f.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去除url中的ip端口信息 负载均衡中可能不一样
     * @param url
     * @return
     */
    private String getUrl(String url) {
        try {
            URL urlInfo = new URL(url);
            StringBuilder urlSB = new StringBuilder();
            urlSB.append(urlInfo.getProtocol()).append("://");
            if (StringUtils.isNotEmpty(urlInfo.getPath())) {
                urlSB.append(urlInfo.getPath());
            }
            if (StringUtils.isNotEmpty(urlInfo.getQuery())) {
                urlSB.append("?").append(urlInfo.getQuery());
            }
            return urlSB.toString();

        } catch (MalformedURLException e) {
            return "";
        }
    }
}
