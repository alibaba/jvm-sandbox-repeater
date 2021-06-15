package com.alibaba.jvm.sandbox.repeater.plugin.apachehttpclient;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * ApacheHttpClient的调用处理器
 *
 * @Author: vivo-孙道明
 * @version 1.0
 * @CreateDate: 2020/11/5 19:28
 */
public class ApacheHttpClientProcessor extends DefaultInvocationProcessor {

    private final String POST = "post";

    private final String DEFAULT_REQUEST_BODY_STR = "apacheHttpClient-plugin request body is not repeatable";

    private final String DEFAULT_RESPONSE_BODY_STR = "apacheHttpClient-plugin response body is not repeatable";



    ApacheHttpClientProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {

        String urlStr = "apacheHttpClient-plugin get urlStr error";
        // org.apache.http.HttpRequest
        Object request = event.argumentArray[1];

        try {
            // org.apache.http.RequestLine
            Object uri = MethodUtils.invokeMethod(request, "getURI");
            String path = (String) MethodUtils.invokeMethod(uri, "getPath");
            urlStr = HttpUtil.getPureUrL(MethodUtils.invokeMethod(uri, "getScheme"),
                    MethodUtils.invokeMethod(uri, "getHost"))+path;
        }catch (Exception e){
            LogUtil.error("apacheHttpClient-plugin assembleMockResponse error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return new Identity(InvokeType.APACHE_HTTP_CLIENT.name(), urlStr, "", null);
    }


    @Override
    public Object[] assembleRequest(BeforeEvent event) {

        try {
            String urlStr = "apacheHttpClient-plugin get urlStr error";
            Object request = event.argumentArray[1];
            // org.apache.http.RequestLine
            Object requestLine = MethodUtils.invokeMethod(request, "getRequestLine");
            Object uri = MethodUtils.invokeMethod(request, "getURI");
            Object queryParam = MethodUtils.invokeMethod(uri, "getQuery");
            Object method = MethodUtils.invokeMethod(requestLine, "getMethod");
            String headersStr = this.getRequestHeadersStr(request);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestMethod", String.valueOf(method));
            // request的header可以简单处理，就用string即可
            params.put("requestHeaders", headersStr);
            params.put("requestParams", HttpUtil.getParamMap(String.valueOf(queryParam)));
            if (POST.equalsIgnoreCase(String.valueOf(method))){
                String bodyStr = this.getRequestBody(request, event.javaClassLoader);
                params.put("requestBody", bodyStr);
//                LogUtil.info("apache-http-client, requestBody: {}", bodyStr);
            }

            return new Object[]{params};
        }catch (Exception e){
            LogUtil.error("apacheHttpClient-plugin assembleRequest error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return new Object[]{};
    }

    private String getRequestHeadersStr(Object request) throws Exception{

        String headersStr = "";
        Object[] headers = (Object[]) MethodUtils.invokeMethod(request, "getAllHeaders");
        if (headers == null){
            return headersStr;
        }
        List headersList = Arrays.asList(headers);
        return String.valueOf(headersList);
    }

    private String getRequestBody(Object request, ClassLoader classLoader) throws Exception {

        Object httpEntity = MethodUtils.invokeMethod(request, "getEntity");
        if (httpEntity == null){
            return StringUtils.EMPTY;
        }

        Long contentLengthObject = (Long)MethodUtils.invokeMethod(httpEntity, "getContentLength");
        if (contentLengthObject == 0){
            return StringUtils.EMPTY;
        }

        // 保险起见在可重复读的时候才去获取body体内容
        Boolean isRepeatable = (Boolean)MethodUtils.invokeMethod(httpEntity, "isRepeatable");
        if (!isRepeatable){
            LogUtil.warn("apacheHttpClient-plugin request body is not repeatable");
            return this.DEFAULT_REQUEST_BODY_STR;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MethodUtils.invokeMethod(httpEntity, "writeTo", baos);
        Charset charset = this.determineCharset(httpEntity, classLoader);

        return new String(baos.toByteArray(), charset);
    }

    private Charset determineCharset(Object httpEntity, ClassLoader classLoader) throws Exception {

        Class<?> aClass = classLoader.loadClass("org.apache.http.entity.ContentType");
        Object contentType = MethodUtils.invokeStaticMethod(aClass, "get", httpEntity);
        if (contentType == null) {
            return StandardCharsets.UTF_8;
        }

        Object charset = MethodUtils.invokeMethod(contentType, "getCharset");
        if (charset == null){
            return StandardCharsets.UTF_8;
        }

        return (Charset)charset;
    }

    @Override
    public Object assembleResponse(Event event) {
        // assembleResponse可能在before事件中被调用，这里只需要在return时间中获取返回值
        if (event.type == Event.Type.RETURN){
            ReturnEvent returnEvent = (ReturnEvent) event;
            // org.apache.http.HttpResponse
            Object response = returnEvent.object;
            try {
                Object statusLine = MethodUtils.invokeMethod(response, "getStatusLine");
                Object statusCode = MethodUtils.invokeMethod(statusLine, "getStatusCode");
                Object reasonPhrase = MethodUtils.invokeMethod(statusLine, "getReasonPhrase");

                Object protocolVersion = MethodUtils.invokeMethod(statusLine, "getProtocolVersion");
                Object protocol = MethodUtils.invokeMethod(protocolVersion, "getProtocol");
                Object major = MethodUtils.invokeMethod(protocolVersion, "getMajor");
                Object minor = MethodUtils.invokeMethod(protocolVersion, "getMinor");
                Map<String, List<String>> headersMap = this.getResponseHeadersMap(response);
                String responseBody = this.getResponseBody(response, response.getClass().getClassLoader());

                Map<String, Object> responseMap = new HashMap<String, Object>();
                responseMap.put("responseHeaders", headersMap);
                responseMap.put("responseProtocol", protocol);
                responseMap.put("responseMajor", major);
                responseMap.put("responseMinor", minor);
                responseMap.put("responseCode", statusCode);
                responseMap.put("responseMessage", reasonPhrase);
                responseMap.put("responseBody", responseBody);

                return responseMap;
            }catch (Exception e){
                LogUtil.error("apacheHttpClient-plugin copy response error",  e);
            }
        }

        return null;
    }

    private Map<String, List<String>> getResponseHeadersMap(Object response) throws Exception {

        Object[] headers = (Object[]) MethodUtils.invokeMethod(response, "getAllHeaders");
        Map<String, List<String>> headersMap = new HashMap<String, List<String>>();
        if (headers == null){
            return new HashMap<String, List<String>>();
        }

        for (Object header : headers) {
            String name = (String) MethodUtils.invokeMethod(header, "getName");
            String value = (String) MethodUtils.invokeMethod(header, "getValue");
            List<String> list = headersMap.get(name);

            if (list == null){
                list = new ArrayList<String>();
                headersMap.put(name, list);
            }
            list.add(value);
        }

        return headersMap;
    }

    private String getResponseBody(Object response, ClassLoader classLoader) throws Exception{

        // org.apache.http.HttpEntity
        Object httpEntity = MethodUtils.invokeMethod(response, "getEntity");
        if (httpEntity == null){
            return StringUtils.EMPTY;
        }

        Long contentLength = (Long)MethodUtils.invokeMethod(httpEntity, "getContentLength");
        if (contentLength == 0){
            return StringUtils.EMPTY;
        }

        Boolean isRepeatable = (Boolean)MethodUtils.invokeMethod(httpEntity, "isRepeatable");
        if (isRepeatable){
            return this.getResponseBodyStr(httpEntity, classLoader);
        }else {
            if (this.canResetContent(httpEntity, classLoader)){
                String responseBodyStr = this.getResponseBodyStr(httpEntity, classLoader);
                // 重新设置返回结果中的流
                this.reSetResponseStream(classLoader, httpEntity, responseBodyStr, false);
                return responseBodyStr;
            }else {
                // 如果当前这个wrappedEntity没有setContent方法，就尝试在当前wrappedEntity内部取属性wrappedEntity
                Object httpEntityInside = this.getWrappedEntity(httpEntity, classLoader);
                if (this.canResetContent(httpEntityInside, classLoader)){
                    // 有些压缩数据需要特殊处理，所以还是从最外层的httpEntity获取数据
                    String responseBodyStr = this.getResponseBodyStr(httpEntity, classLoader);
                    // 重新设置返回结果中的流
                    this.reSetResponseStream(classLoader, httpEntityInside, responseBodyStr, true);
                    // org.apache.http.client.entity.DecompressingEntity，将content置为null，下次读取是触发重新执行getDecompressingStream方法
                    this.setContentNull(httpEntity, classLoader);
                    return responseBodyStr;
                }else {
                    return this.DEFAULT_RESPONSE_BODY_STR;
                }
            }
        }
    }

    private String getResponseBodyStr(Object httpEntity, ClassLoader classLoader) throws Exception{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MethodUtils.invokeMethod(httpEntity, "writeTo", baos);
        Charset charset = this.determineCharset(httpEntity, classLoader);
        return new String(baos.toByteArray(), charset);
    }

    private boolean canResetContent(Object httpEntity, ClassLoader classLoader) throws Exception{

        Object wrappedEntity = this.getWrappedEntity(httpEntity, classLoader);
        Method setContent = MethodUtils.getMatchingMethod(wrappedEntity.getClass(), "setContent", InputStream.class);
        return setContent != null;
    }

    private Object getWrappedEntity(Object httpEntity, ClassLoader classLoader) throws Exception{

        Class<?> wrappedEntityClass = classLoader.loadClass("org.apache.http.entity.HttpEntityWrapper");
        Field wrappedEntityField = FieldUtils.getDeclaredField(wrappedEntityClass, "wrappedEntity", true);
        return wrappedEntityField.get(httpEntity);
    }

    // 这里对请求的流进行了重新复制，不同场景的可靠性需要验证
    private void reSetResponseStream(ClassLoader classLoader, Object httpEntity, String responseBodyStr, Boolean gizp) throws Exception {
        Class<?> wrappedEntityClass = classLoader.loadClass("org.apache.http.entity.HttpEntityWrapper");
        Field wrappedEntityField = FieldUtils.getDeclaredField(wrappedEntityClass, "wrappedEntity", true);
        Object wrappedEntity = wrappedEntityField.get(httpEntity);
        if (gizp){
            MethodUtils.invokeMethod(wrappedEntity, "setContent", this.getGzipByte(responseBodyStr, classLoader, httpEntity));
        }else {
            MethodUtils.invokeMethod(wrappedEntity, "setContent", new ByteArrayInputStream(responseBodyStr.getBytes()));
        }
    }

    private InputStream getGzipByte(String responseBodyStr, ClassLoader classLoader, Object httpEntity) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        Charset charset = this.determineCharset(httpEntity, classLoader);
        gzip = new GZIPOutputStream(out);
        gzip.write(responseBodyStr.getBytes(charset));
        gzip.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void setContentNull(Object httpEntity, ClassLoader classLoader) throws Exception{
        Class<?> decompressingEntity = classLoader.loadClass("org.apache.http.client.entity.DecompressingEntity");
        Field wrappedEntityField = FieldUtils.getDeclaredField(decompressingEntity, "content", true);
        wrappedEntityField.set(httpEntity, null);
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation){

        Map<String, Object> responseMap = (Map<String, Object>) invocation.getResponse();
        if (MapUtils.isEmpty(responseMap)){
            return new Object();
        }

        // 未录制成功的时候直接返回
        if (this.DEFAULT_RESPONSE_BODY_STR.equals(responseMap.get("responseBody"))){
            return new Object();
        }

        try {
            Object mockResponseProtocolVersion = this.getMockResponseProtocolVersion(event, responseMap);
            // 创建返回实体 org.apache.http.message.BasicHttpResponse
            Object basicResponse = this.createResponse(event, responseMap, mockResponseProtocolVersion);
            // 获取返回body体，org.apache.http.HttpResponse
            this.addMockResponseBody(basicResponse, (String) responseMap.get("responseBody"), event.javaClassLoader);
            // 增加header头
            this.addMockResponseHeaders(basicResponse, (Map<String, Object>)responseMap.get("responseHeaders"));

            return this.getMockResponse(basicResponse, event.javaClassLoader);
        }catch (Exception e){
            LogUtil.error("apacheHttpClient-plugin assembleMockResponse error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
            throw new RuntimeException(e);
        }
    }

    private Object createResponse(BeforeEvent event, Map<String, Object> responseMap, Object mockResponseProtocolVersion) throws Exception {
        ClassLoader javaClassLoader = event.javaClassLoader;
        // org.apache.http.impl.execchain.HttpResponseProxy 不同版本这个类是否一致，需要确认
        Class<?> aClass = javaClassLoader.loadClass("org.apache.http.message.BasicHttpResponse");
        Constructor<?>[] constructors = aClass.getConstructors();

        Constructor currentCsc=null;
        //查找到合适的构造器，如果版本不兼容则报错
        for(Constructor constructor:constructors){
            Class<?>[]classes =constructor.getParameterTypes();
            if(classes.length == 3){
                if(classes[0].getCanonicalName().equalsIgnoreCase("org.apache.http.ProtocolVersion")
                        && classes[1].getCanonicalName().equalsIgnoreCase("int")
                        && classes[2].getCanonicalName().equalsIgnoreCase("java.lang.String")){
                    currentCsc=constructor;
                    break;
                }
            }
        }
        if(currentCsc == null){
            throw new RuntimeException("apache-http版本不兼容，请联系平台人员支持兼容的apache-client版本");
        }

        return currentCsc.newInstance(mockResponseProtocolVersion, responseMap.get("responseCode"), responseMap.get("responseMessage"));
    }

    private Object getMockResponseProtocolVersion(BeforeEvent event, Map<String, Object> responseMap) throws Exception{
        ClassLoader javaClassLoader = event.javaClassLoader;
        Class<?> protocolClass = javaClassLoader.loadClass("org.apache.http.ProtocolVersion");
        Constructor<?>[] constructors = protocolClass.getConstructors();

        return constructors[0].newInstance(responseMap.get("responseProtocol"), responseMap.get("responseMajor"), responseMap.get("responseMinor"));
    }

    private void addMockResponseHeaders(Object response, Map<String, Object> headersMap) throws Exception {

        if (MapUtils.isEmpty(headersMap)){
            return;
        }

        for (Map.Entry<String, Object> header : headersMap.entrySet()) {

            String name = header.getKey();
            List<String> values = (List<String>) header.getValue();
            if (StringUtils.isBlank(name) || CollectionUtils.isEmpty(values)){
                continue;
            }

            for (String value : values) {
                MethodUtils.invokeMethod(response, "addHeader", name, value);
            }
        }
    }

    private void addMockResponseBody(Object response, String responseBody, ClassLoader classLoader) throws Exception{
        Class<?> httpEntityClass = classLoader.loadClass("org.apache.http.entity.BasicHttpEntity");
        Object httpEntity = httpEntityClass.newInstance();
        byte[] bytes = responseBody.getBytes();

        InputStream inputStream = new ByteArrayInputStream(bytes);
        MethodUtils.invokeMethod(httpEntity, "setContent", inputStream);
        MethodUtils.invokeMethod(httpEntity, "setContentLength", bytes.length);
        MethodUtils.invokeMethod(response, "setEntity", httpEntity);
    }

    private Object getMockResponse(Object basicResponse, ClassLoader classLoader) throws Exception{
        Class<?> aClass = classLoader.loadClass("org.apache.http.impl.execchain.HttpResponseProxy");
        Constructor<?>[] constructors = aClass.getConstructors();
        constructors[0].setAccessible(true);

        return constructors[0].newInstance(basicResponse, null);
    }
}
