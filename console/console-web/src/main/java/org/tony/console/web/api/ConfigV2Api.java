package org.tony.console.web.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.ModuleConfigBizService;
import org.tony.console.biz.model.StaticConfigVO;
import org.tony.console.common.Result;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/2/21 10:57
 */
@RestController
@RequestMapping("/api/v2/config")
public class ConfigV2Api {

    @Resource
    ModuleConfigBizService moduleConfigBizService;

    @RequestMapping("/static/{appName}/{env}")
    public Result<StaticConfigVO> getConfig(@PathVariable("appName") String appName,
                                            @PathVariable("env") String env) {

        StaticConfigVO staticConfigVO = moduleConfigBizService.getConfig(appName, env);

        return Result.buildSuccess(staticConfigVO, "成功");
    }
}
