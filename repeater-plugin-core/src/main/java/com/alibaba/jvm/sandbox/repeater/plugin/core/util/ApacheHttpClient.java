package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

/**
 * @author peng.hu1
 * @Date 2023/1/17 16:55
 */

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * http请求方法的封装
 */
public class ApacheHttpClient {

    final static Logger Log = LoggerFactory.getLogger(ApacheHttpClient.class);
    CookieStore store;

    private static PoolingHttpClientConnectionManager cm = null;
    private static RequestConfig requestConfig = null;
    private static CloseableHttpClient httpClient = null;
    private static ApacheHttpClient instance = null;


    static {

        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            Log.error("创建SSL连接失败");
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        //多线程调用注意配置，根据线程数设定
        cm.setMaxTotal(10);
        //多线程调用注意配置，根据线程数设定
        cm.setDefaultMaxPerRoute(300);
        requestConfig = RequestConfig.custom()
                //数据传输过程中数据包之间间隔的最大时间
                .setSocketTimeout(20000)
                //连接建立时间，三次握手完成时间
                .setConnectTimeout(3000)
                //重点参数
                .setExpectContinueEnabled(true)
                .setConnectionRequestTimeout(10000)
                //重点参数，在请求之前校验链接是否有效
                .setStaleConnectionCheckEnabled(true)
                .build();

        instance = new ApacheHttpClient();
    }

    public static CloseableHttpClient getHttpClient() {

        if (httpClient == null) {
            synchronized (CloseableHttpClient.class) {
                if (httpClient == null) {
                    httpClient = HttpClients.custom()
                            .setConnectionManager(cm)
                            .build();
                }
            }
        }
        return httpClient;
    }

    public static ApacheHttpClient getInstance() {
        return instance;
    }

    /**
     * 不带请求头的get方法封装
     * @param url
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse get(String url) throws ClientProtocolException, IOException {

        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpclient = getHttpClient();

        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //创建一个Httpget的请求对象

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
        CloseableHttpResponse httpResponse = httpclient.execute(httpget);

        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();

        }
        return httpResponse;

    }

    /**
     * 不带请求头带cookies信息的get方法封装
     * @param url
     * @param cookieList,存放多个cookies信息
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse get(String url,ArrayList cookieList) throws ClientProtocolException, IOException {

        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();

        //创建一个Httpget的请求对象
        HttpGet httpget = new HttpGet(url);
        //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
        CloseableHttpResponse httpResponse = httpclient.execute(httpget);

        return httpResponse;

    }

    /**
     * 带请求头信息的get方法
     * @param url
     * @param headerMap,键值对形式
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse get(String url, HashMap<String,String> headerMap) throws ClientProtocolException, IOException {

        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //创建一个Httpget的请求对象
        HttpGet httpget = new HttpGet(url);
        //加载请求头到httpget对象
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpget.addHeader(entry.getKey(), entry.getValue());
        }
        //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
        CloseableHttpResponse httpResponse = httpclient.execute(httpget);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 带请求头信息带cookies信息的get方法
     * @param url
     * @param headerMap,键值对形式
     * @param cookieList,存放多个cookies信息
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */
    public CloseableHttpResponse get(String url, HashMap<String,String> headerMap,ArrayList cookieList) throws ClientProtocolException, IOException {

        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //创建一个Httpget的请求对象
        HttpGet httpget = new HttpGet(url);
        //加载请求头到httpget对象
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpget.addHeader(entry.getKey(), entry.getValue());
        }
        //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
        CloseableHttpResponse httpResponse = httpclient.execute(httpget);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 带请求头信息带cookies信息的get方法
     * @param url
     * @param headerMap,键值对形式
     * @param body 带body
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */
    public CloseableHttpResponse getWithBody(String url, Map<String,String> headerMap, String body, Map<String, String[]> paramMap) throws ClientProtocolException, IOException {

        CloseableHttpClient httpclient = getHttpClient();

        RequestBuilder builder = RequestBuilder
                .get(url)
                .setConfig(requestConfig);


        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        builder.setEntity(httpEntity);

        //加载请求头到httpget对象
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            String key = entry.getKey();
            if (!"Content-Length".equals(key)) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        List<NameValuePair> params = new LinkedList<>();
        if (paramMap!=null && paramMap.size()>0) {

            for (String key : paramMap.keySet()) {
                String[] vs = paramMap.get(key);
                for (String v : vs) {
                    NameValuePair pair = new BasicNameValuePair(key, v);
                    builder.addParameter(pair);
                }
            }
        }

        //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
        CloseableHttpResponse httpResponse = httpclient.execute(builder.build());
        return httpResponse;
    }


    /**
     * 封装form表单格式的post方法
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse post(String url, Map<String, Object> paramsMap, HashMap<String,String> headerMap) throws ClientProtocolException, IOException {
        //创建一个Httppost的请求对象
        HttpPost httppost = new HttpPost(url);

        Object valueClass =null;

        //获取paramsMap中的value的class，用于判断value的类型
        for (Map.Entry<String, Object> arg : paramsMap.entrySet()) {
            valueClass = arg.getValue();
            break;
        }

        String key = null;
        File value = null;

        //用value的类型和File类型比较，如果类型是File执行上传文件的请求，如果去其他类型则普通表单的请求
        if (valueClass instanceof File) {
            //取出paramsMap里的key和value
            for (Map.Entry<String, Object> arg : paramsMap.entrySet()) {
                key = arg.getKey();
                value = (File) arg.getValue();
                break;
            }

            FileBody bin = new FileBody(value, ContentType.create("image/png", Consts.UTF_8));//创建图片提交主体信息
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .setCharset(Charset.forName("utf-8"))
                    .addPart(key, bin)//添加到entity里
                    .build();
            httppost.setEntity(entity);
        } else {
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            //迭代Map-->取出key，value放到BasicNameValuePair对象中-->添加到list中
            for (String key1 : paramsMap.keySet()){
                pairList.add(new BasicNameValuePair(key1,paramsMap.get(key1).toString()));
            }
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList,"utf-8");
            httppost.setEntity(uefe);
        }
        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //加载请求头到httppost对象
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httppost.addHeader(entry.getKey(), entry.getValue());
        }

        //发送post请求
        CloseableHttpResponse httpResponse = httpclient.execute(httppost);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String cookiename = cookie.getName();
            String cookievalue = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 封装form表单格式带cookies信息的post方法
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @param cookieList cookieList,存放多个cookies信息
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse post(String url, Map<String, Object> paramsMap, HashMap<String,String> headerMap, ArrayList cookieList) throws ClientProtocolException, IOException {
        //创建一个Httppost的请求对象
        HttpPost httppost = new HttpPost(url);

        Object valueClass =null;

        //获取paramsMap中的value的class，用于判断value的类型
        for (Map.Entry<String, Object> arg : paramsMap.entrySet()) {
            valueClass = arg.getValue();
            break;
        }

        String key = null;
        File value = null;

        //用value的类型和File类型比较，如果类型是File执行上传文件的请求，如果去其他类型则普通表单的请求
        if (valueClass instanceof File) {
            //取出paramsMap里的key和value
            for (Map.Entry<String, Object> arg : paramsMap.entrySet()) {
                key = arg.getKey();
                value = (File) arg.getValue();
                break;
            }

            FileBody bin = new FileBody(value, ContentType.create("image/png", Consts.UTF_8));//创建图片提交主体信息
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .setCharset(Charset.forName("utf-8"))
                    .addPart(key, bin)//添加到entity里
                    .build();
            httppost.setEntity(entity);
        } else {
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            //迭代Map-->取出key，value放到BasicNameValuePair对象中-->添加到list中
            for (String key1 : paramsMap.keySet()){
                pairList.add(new BasicNameValuePair(key1,paramsMap.get(key1).toString()));
            }
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList,"utf-8");
            httppost.setEntity(uefe);
        }
        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //加载请求头到httppost对象
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httppost.addHeader(entry.getKey(), entry.getValue());
        }

        //发送post请求
        CloseableHttpResponse httpResponse = httpclient.execute(httppost);
        return httpResponse;
    }

    /**
     * 封装json格式的post方法
     * @param url
     * @param entityString，其实就是设置请求json参数
     * @param headerMap，带请求头
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse post(String url, String entityString, HashMap<String,String> headerMap) throws ClientProtocolException, IOException{
        //创建一个可关闭的HttpClient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //创建一个Httppost的请求对象
        HttpPost httppost = new HttpPost(url);
        //设置payload
        httppost.setEntity(new StringEntity(entityString,"UTF-8"));

        //加载请求头到httppost对象
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httppost.addHeader(entry.getKey(), entry.getValue());
        }

        //发送post请求
        CloseableHttpResponse httpResponse = httpclient.execute(httppost);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 封装json格式带cookies信息的post方法
     * @param url
     * @param entityString，其实就是设置请求json参数
     * @param headerMap，带请求头
     * @param cookieList cookieList,存放多个cookies信息
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse post(String url, String entityString, HashMap<String,String> headerMap, ArrayList cookieList) throws ClientProtocolException, IOException{
        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        //创建一个Httppost的请求对象
        HttpPost httppost = new HttpPost(url);
        //设置payload
        httppost.setEntity(new StringEntity(entityString,"UTF-8"));

        //加载请求头到httppost对象
        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httppost.addHeader(entry.getKey(), entry.getValue());
        }

        //发送post请求
        CloseableHttpResponse httpResponse = httpclient.execute(httppost);
        return httpResponse;
    }

    /**
     * 封装 put请求方法，参数和post方法一样
     * @param url
     * @param entityString，这个主要是设置payload,一般来说就是json串
     * @param headerMap，带请求的头信息，格式是键值对，所以这里使用hashmap
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse put(String url, String entityString, HashMap<String,String> headerMap) throws ClientProtocolException, IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        HttpPut httpput = new HttpPut(url);
        httpput.setEntity(new StringEntity(entityString));

        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpput.addHeader(entry.getKey(), entry.getValue());
        }
        //发送put请求
        CloseableHttpResponse httpResponse = httpclient.execute(httpput);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 封装 put带cookies信息的请求方法，参数和post方法一样
     * @param url
     * @param entityString，这个主要是设置payload,一般来说就是json串
     * @param headerMap，带请求的头信息，格式是键值对，所以这里使用hashmap
     * @param cookieList cookieList,存放多个cookies信息
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */

    public CloseableHttpResponse put(String url, String entityString, HashMap<String,String> headerMap, ArrayList cookieList) throws ClientProtocolException, IOException {
        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        HttpPut httpput = new HttpPut(url);
        httpput.setEntity(new StringEntity(entityString));

        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpput.addHeader(entry.getKey(), entry.getValue());
        }
        //发送put请求
        CloseableHttpResponse httpResponse = httpclient.execute(httpput);
        return httpResponse;
    }

    /**
     * 封装 delete请求方法，参数和get方法一样
     * @param url， 接口url完整地址
     * @return，返回一个response对象，方便进行得到状态码和json解析动作
     * @throws ClientProtocolException
     * @throws IOException
     */


    public CloseableHttpResponse delete(String url) throws ClientProtocolException, IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 设置cookies信息
        store = new BasicCookieStore();
        httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        HttpDelete httpdel = new HttpDelete(url);

        //发送delete请求
        CloseableHttpResponse httpResponse = httpclient.execute(httpdel);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        for (Cookie cookie : cookielist) {
            String name = cookie.getName();
            String value = cookie.getValue();
        }
        return httpResponse;
    }

    /**
     * 封装 delete带cookies信息的请求方法，参数和get方法一样
     * @param url， 接口url完整地址
     * @return，返回一个response对象，方便进行得到状态码和json解析动作
     * @throws ClientProtocolException
     * @throws IOException
     */


    public CloseableHttpResponse delete(String url, ArrayList cookieList) throws ClientProtocolException, IOException {
        // 设置cookies信息
        store = new BasicCookieStore();
        for (Object x:
                cookieList) {
            store.addCookie((Cookie) x);
        }
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(store).build();
        HttpDelete httpdel = new HttpDelete(url);

        //发送delete请求
        CloseableHttpResponse httpResponse = httpclient.execute(httpdel);
        // 读取cookie信息
        List<Cookie> cookielist = store.getCookies();
        return httpResponse;
    }

    /**
     * 获取响应状态码，常用来和TestBase中定义的状态码常量去测试断言使用
     * @param response
     * @return 返回int类型状态码
     */

    public int getStatusCode (CloseableHttpResponse response) {

        int statusCode = response.getStatusLine().getStatusCode();
        Log.info("解析，得到响应状态码:"+ statusCode);
        return statusCode;
    }

    /**
     * 判断响应状态码是否符合预期
     * @param response
     * @param code 预期int类型状态码
     */

    public Boolean checkStatusCode (CloseableHttpResponse response,int code) {

        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode==code;
    }

    /**
     *
     * @param response, 任何请求返回的响应对象
     * @return， 返回响应体的json格式对象，方便接下来对JSON对象内容解析
     * 接下来，一般会继续调用TestUtil类下的json解析方法得到某一个json对象的值
     * @throws ParseException
     * @throws IOException
     */

    public String getResponseString (CloseableHttpResponse response) throws ParseException, IOException {
        String responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
        return responseString;
    }

    /**
     *
     * @param response, 任何请求返回的响应对象
     * @return， 返回响应体的json格式对象，方便接下来对JSON对象内容解析
     * 接下来，一般会继续调用TestUtil类下的json解析方法得到某一个json对象的值
     * @throws ParseException
     * @throws IOException
     */

    public JSONObject getResponseJson (CloseableHttpResponse response) throws ParseException, IOException {
        String responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
        JSONObject responseJson = JSON.parseObject(responseString);
        return responseJson;
    }
}
