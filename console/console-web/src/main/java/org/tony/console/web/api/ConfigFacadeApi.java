package org.tony.console.web.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.AppConfigBizService;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.common.domain.ModuleConfigParams;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.ModuleConfigService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/facade/api")
public class ConfigFacadeApi {
    @Resource
    private ModuleConfigService moduleConfigService;

    @Resource
    AppConfigService appConfigService;

    @Resource
    AppConfigBizService appConfigBizService;

    @RequestMapping("/dyConfig/{appName}/{env}")
    public Result<DynamicConfig> getDynamicConfig(@PathVariable("appName") String appName,
                                           @PathVariable("env") String env) {
        DynamicConfig dynamicConfig = appConfigService.queryDynamicConfig(appName, env);
        return Result.builder()
                .success(true)
                .message("success")
                .data(dynamicConfig)
                .build();
    }

    @RequestMapping("/config/{appName}/{env}")
    public Result<RepeaterConfig> getConfig(@PathVariable("appName") String appName,
                                            @PathVariable("env") String env) {
        ModuleConfigParams params = new ModuleConfigParams();
        params.setAppName(appName);
        params.setEnvironment(env);
        Result<ModuleConfigBO> result = moduleConfigService.query(params);
        // fix issue #83 npe
        return Result.builder()
                .success(result.isSuccess())
                .message(result.getMessage())
                .data(result.getData() == null ? null : result.getData().getConfigModel())
                .build();
    }

    @RequestMapping("/v2/config/{appName}/{env}")
    public Result<RepeaterConfig> getConfigV2(@PathVariable("appName") String appName,
                                            @PathVariable("env") String env) {
        ModuleConfigParams params = new ModuleConfigParams();
        params.setAppName(appName);
        params.setEnvironment(env);
        Result<ModuleConfigBO> result = moduleConfigService.queryWithDefault(params);
        // fix issue #83 npe
        return Result.builder()
                .success(result.isSuccess())
                .message(result.getMessage())
                .data(result.getData() == null ? null : result.getData().getConfigModel())
                .build();
    }

    @RequestMapping("/groovy/{appName}/{env}")
    public Result<List<GroovyConfig>> getGroovyConfig(@PathVariable("appName") String appName,
                                                  @PathVariable("env") String env) {

        List<GroovyConfig> groovyConfigs = appConfigBizService.queryGroovyConfigListForAgent(appName, env);
        return Result.builder()
                .success(true)
                .message("success")
                .data(groovyConfigs)
                .build();
    }

    @RequestMapping("/groovyS/{appName}/{id}")
    public Result<GroovyConfig> getGroovyConfig(@PathVariable("appName") String appName,
                                                      @PathVariable("id") Long id) {

        GroovyConfig config = appConfigBizService.queryGroovyConfigForAgent(appName, id);
        return Result.builder()
                .success(true)
                .message("success")
                .data(config)
                .build();
    }

}
