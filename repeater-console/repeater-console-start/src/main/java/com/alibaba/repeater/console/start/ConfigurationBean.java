package com.alibaba.repeater.console.start;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import com.alibaba.repeater.client.SpringContextAware;
import com.alibaba.repeater.console.service.serialize.CustomJavaTimeModule;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@Configuration
public class ConfigurationBean {

    /**
     * 注入repeater感知spring context的hook，用于java回放
     *
     * @return SpringContextAware
     */
    @Bean
    public SpringContextAware getSpringContextAware() {
        return new SpringContextAware();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.locale(Locale.getDefault());
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
            builder.modules(new CustomJavaTimeModule());
        };
    }
}
