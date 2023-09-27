package com.alibaba.jvm.sandbox.repeater.plugin.okhttp.util;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;


/**
 * okhttp 插件工具类
 *
 * @Author: vivo-孙道明
 * @version 1.0
 * @CreateDate: 2020/11/1 21:15
 */
public class HttpOkUtil {

    // request真实对应的类okhttp3.Request
    public static String getBody(Object request){

        try {
            Object body = MethodUtils.invokeMethod(request, "body");
            if (body == null){
                return StringUtils.EMPTY;
            }

            Field contentType = FieldUtils.getDeclaredField(body.getClass(), "val$contentType", true);
            Field content = FieldUtils.getDeclaredField(body.getClass(), "val$content", true);
            if (null == contentType) {
                // okhttp4
                contentType = FieldUtils.getDeclaredField(body.getClass(), "$contentType", true);
                content= FieldUtils.getDeclaredField(body.getClass(), "$this_toRequestBody", true);
            }
            if (null == contentType) {
                LogUtil.error("okhttp version not compatibility ");
                return StringUtils.EMPTY;
            }
            // okhttp3.MediaType
            Object MediaType = contentType.get(body);
            Object bodyByte = content.get(body);
            Object charset = MethodUtils.invokeMethod(MediaType, "charset");

            if (bodyByte == null) {
                return StringUtils.EMPTY;
            }

            if (charset == null) {
                return new String((byte[])bodyByte);
            }

            return new String((byte[])bodyByte, charset.toString());
        } catch (Exception e) {
            LogUtil.error("HttpOkRecordModifierHandler.getBody error:", e);
        }
        return "okhttp get request body error";
    }

    public static InputStream getStringInputStream(String paramStr){

        if(StringUtils.isBlank(paramStr)){
            return null;
        }

        return new ByteArrayInputStream(paramStr.getBytes());
    }
}
