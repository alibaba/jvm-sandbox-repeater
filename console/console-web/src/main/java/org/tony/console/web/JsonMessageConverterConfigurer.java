package org.tony.console.web;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/6 19:50
 */
@Configuration
public class JsonMessageConverterConfigurer implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        // 自定义配置...
        FastJsonConfig config = new FastJsonConfig();
        config.setWriterFeatures(JSONWriter.Feature.WriteEnumsUsingName, JSONWriter.Feature.IgnoreNonFieldGetter);
        converter.setFastJsonConfig(config);

        // spring boot高版本无需配置，低版本不配置报错：Content-Type cannot contain wildcard type '*'
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);

        JSON.register(JsonObject.class, new ObjectWriter<JsonObject>() {
            @Override
            public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
                JsonObject jsonObject = (JsonObject) object;
                if (jsonObject == null) {
                    jsonWriter.writeNull();
                }
                Gson gson = new Gson();
                String s = gson.toJson(jsonObject);
                jsonWriter.writeString(s);
            }
        });

        converters.add(0,converter);
    }
}
