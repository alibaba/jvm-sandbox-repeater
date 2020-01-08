package com.alibaba.repeater.console.start.controller.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;
import com.alibaba.repeater.console.service.ModuleConfigService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * {@link ConfigFacadeApi} Demo工程；作为repeater录制回放的配置管理服务
 * <p>
 *
 * @author zhaoyb1990
 */
@RestController
@RequestMapping("/facade/api")
public class ConfigFacadeApi {

    @Resource
    private ModuleConfigService moduleConfigService;

    @RequestMapping("/config/{appName}/{env}")
    public RepeaterResult<RepeaterConfig> getConfig(@PathVariable("appName") String appName,
                                                    @PathVariable("env") String env) {
        ModuleConfigParams params = new ModuleConfigParams();
        params.setAppName(appName);
        params.setEnvironment(env);
        RepeaterResult<ModuleConfigBO> result = moduleConfigService.query(params);
        return RepeaterResult.builder().success(result.isSuccess()).message(result.getMessage()).data(result.getData().getConfigModel()).build();
    }

}
