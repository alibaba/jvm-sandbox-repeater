package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyType;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:51
 */
public interface GroovySubscriber {

    public void subscribe(GroovyConfig groovyObject);

    public GroovyType getType();
}
