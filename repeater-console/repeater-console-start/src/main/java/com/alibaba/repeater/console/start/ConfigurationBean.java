package com.alibaba.repeater.console.start;

import com.alibaba.repeater.client.SpringContextAware;

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
}
