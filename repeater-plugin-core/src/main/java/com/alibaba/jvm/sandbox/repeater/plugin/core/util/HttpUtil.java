package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link HttpUtil} 基于{@link OkHttpClient}封装的http请求工具
 * <p>
 *
 * @author zhaoyb1990
 */
public class HttpUtil {

    private static final String QUESTION_SEPARATE = "?";

    private static final String PARAM_SEPARATE = "&";

    private static final String KV_SEPARATE = "=";

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(10, 60, TimeUnit.NANOSECONDS))
            .build();


    public static Resp doGet(String url, int retryTime) {
        return executeRequest(new Request.Builder().get().url(url).build(), retryTime);
    }


    /**
     * 执行GET请求，返回body的string
     *
     * @param url 请求URL
     * @return response的内容
     */
    public static Resp doGet(String url) {
        return executeRequest(new Request.Builder().get().url(url).build());
    }

    /**
     * 执行GET请求，返回body的string
     *
     * @param url 请求URL
     * @return response的内容
     */
    public static Resp doGetWithHeader(String url, Map<String, String> headers) {
        final Request.Builder builder = new Request.Builder().get().url(url);
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        return executeRequest(builder.build());
    }


    public static String getPureUrL(Object scheme, Object host){
        return String.valueOf(scheme) + "://" + String.valueOf(host);
    }

    public static Map<String, String> getParamMap(String paramStr){

        Map<String, String> paramMap = new HashMap<String, String>();
        if (StringUtils.isBlank(paramStr) || "null".equalsIgnoreCase(paramStr)){
            return paramMap;
        }

        String[] split = paramStr.split(PARAM_SEPARATE);
        if (split.length == 0){
            return paramMap;
        }

        for (String s : split) {
            String[] param = s.split(KV_SEPARATE);
            if (param.length == 0) {
                continue;
            }
            paramMap.put(param[0], param[1]);
        }

        return paramMap;
    }

    /**
     * 执行GET请求，返回body的string
     *
     * @param url 请求URL
     * @return response的内容
     */
    public static Resp doGet(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        if (!StringUtils.contains(url, QUESTION_SEPARATE)) {
            builder.append(QUESTION_SEPARATE).append("_r=1");
        }
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(PARAM_SEPARATE)
                        .append(entry.getKey())
                        .append(KV_SEPARATE)
                        .append(entry.getValue());
            }
        }
        return doGet(builder.toString());
    }

    /**
     * 执行post请求
     *
     * @param url 请求URL
     * @return response的内容
     */
    public static Resp doPost(String url) {
        return doPost(url, null);
    }

    /**
     * 执行post请求
     *
     * @param url    请求地址
     * @param params 参数
     * @return response的内容
     */
    public static Resp doPost(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().post(builder.build()).url(url).build();
        return executeRequest(request);
    }

    public static Resp fileUpload(String url,
                                  String method,
                                  Map<String, String> headers,
                                  Map<String, String[]> paramMap,
                                  String filename,
                                  String partName,
                                  byte[] fileContent
                                  ) {
        HttpMethod resolve = HttpMethod.resolve(method);

        if (resolve == null || !HttpMethod.POST.equals(resolve)) {
            return Resp.builder().code(500).message("Unsupported multipart http method : " + method).build();
        }

        MultipartBody.Builder fb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(partName, filename,
                        RequestBody.create(MediaType.parse("multipart/form-data"), fileContent));

        StringBuilder urlBuilder = new StringBuilder(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            if (!StringUtils.contains(url, QUESTION_SEPARATE)) {
                urlBuilder.append(QUESTION_SEPARATE).append("_r=1");
            }
            for (Map.Entry<String,String[]> entry : paramMap.entrySet()) {
                for (String value : entry.getValue()) {
                    urlBuilder.append(PARAM_SEPARATE)
                            .append(entry.getKey())
                            .append(KV_SEPARATE)
                            .append(value);
                }
            }
        }

        Request.Builder rb = new Request.Builder()
                .url(urlBuilder.toString())
                .post(fb.build());

        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                rb.header(entry.getKey(), entry.getValue());
            }
        }

        return executeRequest(rb.build(), 0);
    }

    /**
     * 调用回放方法
     *
     * @param url       url地址
     * @param method    请求方式 POST/GET
     * @param headers   请求头
     * @param paramsMap 请求参数
     * @return Resp
     */
    public static Resp invoke(String url,
                              String method,
                              Map<String, String> headers,
                              Map<String, String[]> paramsMap,
                              String body) {
        HttpMethod resolve = HttpMethod.resolve(method);

        if (resolve == null) {
            return Resp.builder().code(500).message("Unsupported http method : " + method).build();
        }

        switch (HttpMethod.resolve(method)) {
            case GET:
                return invokeGet(url, headers, paramsMap, 0, body);
            case POST:
                return invokePost(url, headers, paramsMap, body, 0, null);
            case PUT:
                return invokePut(url, headers, paramsMap, body, 0);
            case HEAD:
            case PATCH:
            case DELETE:
                return invokeDelete(url, headers, paramsMap, body, 0);
            case OPTIONS:
            case TRACE:
            default:
                return Resp.builder().code(500).message("Unsupported http method : " + method).build();
        }
    }

    static class GetBodyBuilder extends Request.Builder {
        public Request.Builder get(RequestBody body) {
            this.post(body);
            try {
                Field field = Request.Builder.class.getDeclaredField("method");
                field.setAccessible(true);
                field.set(this, "GET");
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Couldn't set dirty reflection", e);
            }
            return this;
        }
    }

    private static Resp invokeGetWithBody(String url,
                                          Map<String, String> headers,
                                          Map<String, String[]> paramsMap, String body) {
        ApacheHttpClient client = ApacheHttpClient.getInstance();
        try {
            CloseableHttpResponse response = client.getWithBody(url, headers, body, paramsMap);

            int code = response.getStatusLine().getStatusCode();
            String content = client.getResponseString(response);

            if (code == 200) {
                return Resp.builder().code(code)
                        .body(content)
                        .message("success")
                        .build();
            } else {
                return Resp.builder().code(code)
                        .body(content)
                        .message("Invoke failed, status code is not 200")
                        .build();
            }


        } catch (IOException e) {
            LogUtil.error("http invoke error", e);
            return Resp.builder().code(500)
                    .message("Invoke occurred exception, message=" + e.getMessage())
                    .build();
        }
    }

    /**
     * Get方法请求
     *
     * @param url       url地址
     * @param headers   请求头
     * @param paramsMap 请求参数
     * @return resp
     */
    private static Resp invokeGet(String url,
                                  Map<String, String> headers,
                                  Map<String, String[]> paramsMap, int retryTime, String body) {
        if (body!=null && body.length()>0) {
            return invokeGetWithBody(url, headers, paramsMap, body);
        }

        HttpUrl hu = HttpUrl.parse(url);
        if (hu == null) {
            return Resp.builder().code(500).message("Parse http url failed,url=" + url).build();
        }
        if (MapUtils.isNotEmpty(paramsMap)) {
            HttpUrl.Builder builder = hu.newBuilder();
            for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
                for (String value : entry.getValue()) {
                    builder.addQueryParameter(entry.getKey(), value);
                }
            }
            hu = builder.build();
        }
        Request.Builder rb = new Request.Builder().get().url(hu);
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                rb.header(entry.getKey(), entry.getValue());
            }
        }
        return executeRequest(rb.build(), retryTime);
    }

    /**
     * Get方法请求
     *
     * @param url       url地址
     * @param headers   请求头
     * @param paramsMap 请求参数
     * @return resp
     */
    private static Resp invokeGet(String url,
                                  Map<String, String> headers,
                                  Map<String, String[]> paramsMap) {
        return invokeGet(url, headers, paramsMap, 3, null);
    }

    /**
     * Post方法请求
     *
     * @param url       url地址
     * @param headers   请求头
     * @param paramsMap 请求参数
     * @return Resp
     */
    private static Resp invokePost(String url,
                                   Map<String, String> headers,
                                   Map<String, String[]> paramsMap,
                                   String body,
                                   int retryTime,
                                   HttpMethod method
                                   ) {
        if (StringUtils.isNotEmpty(body)) {
            return invokePostBody(url, headers, paramsMap, body, method);
        }
        FormBody.Builder fb = new FormBody.Builder();
        if (MapUtils.isNotEmpty(paramsMap)) {
            for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
                for (String value : entry.getValue()) {
                    fb.add(entry.getKey(), value);
                }
            }
        }
        Request.Builder rb = new Request.Builder()
                .url(url);

        if (method == null) {
            rb.post(fb.build());
        } else {
            switch (method) {
                case PUT:
                    rb.put(fb.build());
                    break;
                case POST:
                    rb.post(fb.build());
                    break;
                case DELETE:
                    rb.delete(fb.build());
                    break;
            }
        }

        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                rb.header(entry.getKey(), entry.getValue());
            }
        }
        return executeRequest(rb.build(), retryTime);
    }

    private static Resp invokeDelete(String url,
                                     Map<String, String> headers,
                                     Map<String, String[]> paramsMap,
                                     String body,
                                     int retryTime) {
        return invokePost(url, headers, paramsMap, body, retryTime, HttpMethod.DELETE);
    }

    private static Resp invokePut(String url,
                                   Map<String, String> headers,
                                   Map<String, String[]> paramsMap,
                                   String body,
                                   int retryTime) {
        return invokePost(url, headers, paramsMap, body, retryTime, HttpMethod.PUT);
    }

    /**
     * Post方法请求
     *
     * @param url       url地址
     * @param headers   请求头
     * @param paramsMap 请求参数
     * @return resp
     */
    private static Resp invokePost(String url,
                                   Map<String, String> headers,
                                   Map<String, String[]> paramsMap,
                                   String body) {
        return invokePost(url, headers, paramsMap, body, 3, null);
    }


    /**
     * Post方法请求
     *
     * @param url     url地址
     * @param headers 请求头
     * @param body    请求body
     * @return resp
     */
    public static Resp invokePostBody(String url,
                                      Map<String, String> headers,
                                      String body) {
        return invokePostBody(url, headers, null ,body, null);
    }

    /**
     * Post方法请求
     *
     * @param url     url地址
     * @param headers 请求头
     * @param paramMap 请求参数
     * @param body    请求body
     * @return resp
     */
    public static Resp invokePostBody(String url,
                                      Map<String, String> headers,
                                      Map<String, String[]> paramMap,
                                      String body,
                                      HttpMethod method) {
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            contentType = headers.get("content-type");
        }
        if (contentType == null) {
            contentType = "application/x-www-form-urlencoded; charset=utf-8";
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        // fix issue #43
        if (MapUtils.isNotEmpty(paramMap)) {
            if (!StringUtils.contains(url, QUESTION_SEPARATE)) {
                urlBuilder.append(QUESTION_SEPARATE).append("_r=1");
            }
            for (Map.Entry<String,String[]> entry : paramMap.entrySet()) {
                for (String value : entry.getValue()) {
                    urlBuilder.append(PARAM_SEPARATE)
                            .append(entry.getKey())
                            .append(KV_SEPARATE)
                            .append(value);
                }
            }
        }
        RequestBody b = RequestBody.create(MediaType.parse(contentType), body);

        Request.Builder rb = new Request
                .Builder()
                .url(urlBuilder.toString());

        if (method==null) {
            rb.post(b);
        } else {
            switch (method) {
                case PUT:
                    rb.put(b);
                    break;
                case POST:
                    rb.post(b);
                    break;
            }
        }

        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                rb.header(entry.getKey(), entry.getValue());
            }
        }
        // fix issue #70
        return executeRequest(rb.build(), 0);
    }

    /**
     * 执行request
     *
     * @param request 请求
     * @return body字符串
     */
    public static Resp executeRequest(Request request) {
        return executeRequest(request, 3);
    }

    /**
     * 执行request;
     *
     * @param request   请求
     * @param retryTime 重试次数
     * @return body字符串
     */
    private static Resp executeRequest(Request request, int retryTime) {
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Resp.builder().code(response.code())
                        .body(bodyToString(response.body())).build();
            }
            if (--retryTime > 0) {
                TimeUnit.MILLISECONDS.sleep(100);
                return executeRequest(request, retryTime);
            }
            return Resp.builder().code(response.code())
                    .body(bodyToString(response.body()))
                    .message("Invoke failed, status code is not 200")
                    .build();
        } catch (Exception e) {
            if (--retryTime > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e1) {
                    // ignore
                }
                return executeRequest(request, retryTime);
            }
            return Resp.builder().code(500)
                    .message("Invoke occurred exception, request=" + request.toString() + ";message=" + e.getMessage())
                    .build();
        }
    }

    private static String bodyToString(ResponseBody body) throws IOException {
        return body == null ? "" : body.string();
    }

    enum HttpMethod {

        /**
         *
         */
        GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

        private static final Map<String, HttpMethod> CACHED = new HashMap<String, HttpMethod>(16);

        static {
            for (HttpMethod httpMethod : values()) {
                CACHED.put(httpMethod.name(), httpMethod);
            }
        }


        public static HttpMethod resolve(String method) {
            return (method != null ? CACHED.get(method) : null);
        }

        public boolean matches(String method) {
            return (this == resolve(method));
        }
    }

    public static class Resp {
        private int code;
        private String body;
        private String message;

        @ConstructorProperties({"code", "body", "message"})
        Resp(int code, String body, String message) {
            this.code = code;
            this.body = body;
            this.message = message;
        }

        public static RespBuilder builder() {
            return new RespBuilder();
        }

        public boolean isSuccess() {
            return code >= 200 && code <= 300;
        }

        public int getCode() {
            return this.code;
        }

        public String getBody() {
            return this.body;
        }

        public String getMessage() {
            return this.message;
        }

        @Override
        public String toString() {
            return "HttpUtil.Resp(code=" + this.getCode() + ", body=" + this.getBody() + ", message=" + this.getMessage() + ")";
        }

        public static class RespBuilder {
            private int code;
            private String body;
            private String message;

            RespBuilder() {
            }

            public RespBuilder code(int code) {
                this.code = code;
                return this;
            }

            public RespBuilder body(String body) {
                this.body = body;
                return this;
            }

            public RespBuilder message(String message) {
                this.message = message;
                return this;
            }

            public Resp build() {
                return new Resp(this.code, this.body, this.message);
            }

            @Override
            public String toString() {
                return "HttpUtil.Resp.RespBuilder(code=" + this.code + ", body=" + this.body + ", message=" + this.message + ")";
            }
        }
    }

    public static void main(String args[]) {
        String url="http://localhost/v1/in/pad/shopping-cart?hash_type=sha256&app_id=1000069&lang=no&region=NO";
        String body = "{\n" +
                "  \"cart_no\": \"623050641481755100\",\n" +
                "  \"region\": \"NO\",\n" +
                "  \"salesman_id\":\"qichao.qian\"\n" +
                "}";
        Map<String, String> headers = new HashMap<>();
        headers.put("region", "NO");
        headers.put("shop-code", "DP000286");
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", String.valueOf(body.length()));
        Resp resp = HttpUtil.invokeGetWithBody(url, headers, null, body);
        System.out.println(resp);
    }
}
