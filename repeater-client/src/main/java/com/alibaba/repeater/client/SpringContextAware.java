package com.alibaba.repeater.client;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link SpringContextAware} 如果希望能过对spring内置的bean进行录制回放；可以注入{@code SpringContextAware}即可实现回放
 * <p>
 *
 * @author zhaoyb1990
 */
public class SpringContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextContainer.getInstance().setContext(context);
    }
}
