package org.tony.console.web.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.ModuleConfigBizService;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.web.auth.UserInfoCache;
import org.tony.console.web.model.UpdateCompareConfigRequest;
import org.tony.console.web.model.UpdateConfigRequest;
import org.tony.console.web.model.UpdateDynamicConfigRequest;
import org.tony.console.biz.request.UpdateStaticConfigRequest;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2022/12/1 16:14
 */
@RestController
@RequestMapping("/api/v1/config")
public class ConfigApi {

    @Resource
    private ModuleConfigBizService moduleConfigBizService;

    @Resource
    AppConfigService appConfigService;

    @ResponseBody
    @RequestMapping("update")
    public Result updateConfig(@RequestBody UpdateConfigRequest request) {

        try {
            request.check();
            ModuleConfigBO configBO = new ModuleConfigBO();
            configBO.setConfigModel(request.getRepeaterConfig());
            configBO.setAppName(request.getAppName());
            configBO.setEnvironment(request.getEnv());

            moduleConfigBizService.saveOrUpdate(configBO);

            return Result.buildSuccess(null, "保存成功");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping("updateStatic")
    public Result updateConfig(@RequestBody UpdateStaticConfigRequest request) {

        try {
            request.setUser(UserInfoCache.getUser());
            moduleConfigBizService.updateStaticConfig(request);
            return Result.buildSuccess(null, "保存成功");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping(value = "compareConfig/query", method = RequestMethod.GET)
    public Result<AppCompareConfigDO> queryCompareConfig(@RequestParam String appName) {

        if (StringUtils.isBlank(appName)) {
            return Result.buildFail("appName is null");
        }

        return Result.buildSuccess(appConfigService.queryCompareConfig(appName), "查询成功");
    }

    @ResponseBody
    @RequestMapping(value = "compareConfig/update", method = RequestMethod.POST)
    public Result updateCompareConfig(@RequestBody UpdateCompareConfigRequest request) {
        try {
            request.check();
            appConfigService.saveOrUpdateCompareConfig(request.getAppName(), request.getAppCompareConfig());
            return Result.buildSuccess(null, "保存成功");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "dynamicConfig/query", method = RequestMethod.GET)
    public Result<DynamicConfig> queryCompareConfig(@RequestParam String appName, @RequestParam String env) {

        if (StringUtils.isBlank(appName)) {
            return Result.buildFail("appName is null");
        }

        if (StringUtils.isBlank(env)) {
            return Result.buildFail("env is null");
        }

        return Result.buildSuccess(appConfigService.queryDynamicConfig(appName, env), "查询成功");
    }

    @ResponseBody
    @RequestMapping(value = "dynamicConfig/update", method = RequestMethod.POST)
    public Result updateCompareConfig(@RequestBody UpdateDynamicConfigRequest request) {
        try {
            request.check();
            appConfigService.saveOrUpdateDynamicConfig(request.getAppName(), request.getEnv(), request.getDynamicConfig());
            moduleConfigBizService.pushConfigToAgent(request.getAppName(), request.getEnv(), 1);
            return Result.buildSuccess(null, "保存成功");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }
}
