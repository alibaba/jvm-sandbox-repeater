package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:50
 */
public interface GroovyPublisher {

    /**
     * 添加订阅者
     * @param subscriber
     */
    public void add(GroovySubscriber subscriber);

    /**
     * 发布事件
     * @param groovyObject
     */
    public void publish(GroovyConfig groovyObject);

    /**
     * 移除订阅者
     * @param subscriber
     */
    public void remove(GroovySubscriber subscriber);
}
