package com.alibaba.repeater.console.start.controller;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * {@link ConfigFacadeApi} Demo工程；作为repeater录制回放的配置管理服务
 * <p>
 *
 * @author zhaoyb1990
 */
@RestController
@RequestMapping("/facade/api")
public class ConfigFacadeApi {

    @RequestMapping("/config/{appName}/{env}")
    public RepeaterResult<RepeaterConfig> getConfig(@PathVariable("appName") String appName,
                                                    @PathVariable("env") String env) {
        // 自己存配置；目前直接Mock了一份
        RepeaterConfig config = new RepeaterConfig();
        List<Behavior> behaviors = Lists.newArrayList();
        config.setPluginIdentities(Lists.newArrayList("http", "java-entrance", "java-subInvoke", "mybatis", "ibatis"));
        // 回放器
        config.setRepeatIdentities(Lists.newArrayList("java", "http"));
        // 白名单列表
        config.setHttpEntrancePatterns(Lists.newArrayList("^/regress/.*$"));
        // java入口方法
        behaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "getRegress"));
        config.setJavaEntranceBehaviors(behaviors);
        List<Behavior> subBehaviors = Lists.newArrayList();
        // java调用插件
        subBehaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "getRegressInner"));
        subBehaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "findPartner"));
        subBehaviors.add(new Behavior("com.alibaba.repeater.console.service.impl.RegressServiceImpl", "slogan"));
        config.setJavaSubInvokeBehaviors(subBehaviors);
        config.setUseTtl(true);
        return RepeaterResult.builder().success(true).message("operate success").data(config).build();
    }

}
