package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:49
 */
public class GroovyEngine {

    public static void init(List<GroovyConfig> groovyConfigList) {
        for (GroovyConfig groovyConfig : groovyConfigList) {
            GroovyEventBus.instance().publish(groovyConfig);
        }
    }

    public static void flush(GroovyConfig groovyConfig) {
        GroovyEventBus.instance().publish(groovyConfig);
    }
}
