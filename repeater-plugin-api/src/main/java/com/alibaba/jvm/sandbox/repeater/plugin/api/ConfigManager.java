package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;

import java.util.List;

/**
 * {@link ConfigManager} 配置拉取服务
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ConfigManager {

    /**
     * 拉取配置
     *
     * @return 返回拉取的配置
     */
    RepeaterResult<RepeaterConfig> pullConfig();

    /**
     * 日志级别
     * @return
     */
    RepeaterResult<DynamicConfig> pullDynamicConfig();

    /**
     * 拉取groovy配置
     * @return
     */
    RepeaterResult<List<GroovyConfig>> pullGroovyConfig();


    RepeaterResult<GroovyConfig> pullGroovyConfig(String id);
}
