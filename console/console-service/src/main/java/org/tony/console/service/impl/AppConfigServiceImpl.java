package org.tony.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.mapper.AppConfigMapper;
import org.tony.console.db.model.AppConfig;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.service.model.AppConfigType;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author peng.hu1
 * @Date 2022/12/1 20:57
 */
@Component
public class AppConfigServiceImpl implements AppConfigService {

    @Resource
    AppConfigMapper appConfigMapper;

    @Override
    public AppDailyTestConfigDTO queryDailyTest(String appName) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.DAILY_TEST_CONFIG.code, Env.ALL.name().toLowerCase()
        );

        if (CollectionUtils.isEmpty(appConfigs)) {
            return new AppDailyTestConfigDTO();
        }
        AppConfig config = appConfigs.get(0);
        return JSON.parseObject(config.getConfig(), AppDailyTestConfigDTO.class);
    }

    @Override
    public void setDailyTest(String appName, AppDailyTestConfigDTO appDailyTestConfigDTO) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.DAILY_TEST_CONFIG.code, Env.ALL.name().toLowerCase()
        );

        if (CollectionUtils.isEmpty(appConfigs)) {
            AppConfig appConfig = new AppConfig();
            appConfig.setAppName(appName);
            appConfig.setType(AppConfigType.DAILY_TEST_CONFIG.code);
            appConfig.setConfig(JSON.toJSONString(appDailyTestConfigDTO, SerializerFeature.WriteClassName));
            appConfig.setEnv( Env.ALL.name().toLowerCase());
            appConfigMapper.insert(appConfig);
        }

        AppConfig appConfig = appConfigs.get(0);
        appConfig.setConfig(JSON.toJSONString(appDailyTestConfigDTO, SerializerFeature.WriteClassName));

        appConfigMapper.update(appConfig);
    }

    @Override
    public Map<String, AppDailyTestConfigDTO> queryDailyTestConfig() {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfigByTye(AppConfigType.DAILY_TEST_CONFIG.code);
        if (CollectionUtils.isEmpty(appConfigs)) {
            return new HashMap<>();
        }


        Map<String, AppDailyTestConfigDTO> map = new HashMap<>();

        for (AppConfig appConfig: appConfigs) {
            map.put(appConfig.getAppName(), JSON.parseObject(appConfig.getConfig(), AppDailyTestConfigDTO.class));
        }

        return map;
    }

    @Override
    public AppTestTaskSetDTO queryTestTaskSet(String appName, Env env) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.TEST_TASK_SET_CONFIG.code, env.name().toLowerCase()
        );

        if (CollectionUtils.isEmpty(appConfigs)) {
            return new AppTestTaskSetDTO();
        }

        AppConfig config = appConfigs.get(0);
        return JSON.parseObject(config.getConfig(), AppTestTaskSetDTO.class);
    }

    @Override
    public void saveTestTaskSet(String appName, Env env, AppTestTaskSetDTO appTestTaskSetDTO) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.TEST_TASK_SET_CONFIG.code, env.name().toLowerCase()
        );

        if (CollectionUtils.isEmpty(appConfigs)) {
            AppConfig appConfig = new AppConfig();
            appConfig.setAppName(appName);
            appConfig.setType(AppConfigType.TEST_TASK_SET_CONFIG.code);
            appConfig.setConfig(JSON.toJSONString(appTestTaskSetDTO, SerializerFeature.WriteClassName));
            appConfig.setEnv(env.name().toLowerCase());
            appConfigMapper.insert(appConfig);
        }

        AppConfig appConfig = appConfigs.get(0);
        appConfig.setConfig(JSON.toJSONString(appTestTaskSetDTO, SerializerFeature.WriteClassName));

        appConfigMapper.update(appConfig);
    }

    @Override
    public AppCompareConfigDO queryCompareConfig(String appName) {

        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.GLOBAL_COMPARE_CONFIG.code, "all"
        );
        if (CollectionUtils.isEmpty(appConfigs)) {
            return new AppCompareConfigDO();
        }

        AppConfig config = appConfigs.get(0);
        return JSON.parseObject(config.getConfig(), AppCompareConfigDO.class);
    }

    @Override
    public DynamicConfig queryDynamicConfig(String appName, String env) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(
                appName, AppConfigType.DYNAMIC_CONFIG.code, env
        );
        if (CollectionUtils.isEmpty(appConfigs)) {
            return new DynamicConfig();
        }

        AppConfig config = appConfigs.get(0);
        return JSON.parseObject(config.getConfig(), DynamicConfig.class);
    }

    @Override
    public void saveOrUpdateDynamicConfig(String appName, String env, DynamicConfig dynamicConfig) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(appName, AppConfigType.DYNAMIC_CONFIG.code, env);
        if (CollectionUtils.isEmpty(appConfigs)) {
            AppConfig appConfig = new AppConfig();
            appConfig.setAppName(appName);
            appConfig.setType(AppConfigType.DYNAMIC_CONFIG.code);
            appConfig.setConfig(JSON.toJSONString(dynamicConfig, SerializerFeature.WriteClassName));
            appConfig.setEnv(env);
            appConfigMapper.insert(appConfig);
            return;
        }

        AppConfig appConfig = appConfigs.get(0);
        appConfig.setConfig(JSON.toJSONString(dynamicConfig, SerializerFeature.WriteClassName));

        appConfigMapper.update(appConfig);
    }

    @Override
    public void saveOrUpdateCompareConfig(String appName, AppCompareConfigDO appCompareConfigDO) {
        List<AppConfig> appConfigs = appConfigMapper.queryAppConfig(appName, AppConfigType.GLOBAL_COMPARE_CONFIG.code, "all");
        if (CollectionUtils.isEmpty(appConfigs)) {
            AppConfig appConfig = new AppConfig();
            appConfig.setAppName(appName);
            appConfig.setType(AppConfigType.GLOBAL_COMPARE_CONFIG.code);
            appConfig.setConfig(JSON.toJSONString(appCompareConfigDO, SerializerFeature.WriteClassName));
            appConfig.setEnv("all");
            appConfigMapper.insert(appConfig);
            return;
        }

        AppConfig appConfig = appConfigs.get(0);
        appConfig.setConfig(JSON.toJSONString(appCompareConfigDO, SerializerFeature.WriteClassName));

        appConfigMapper.update(appConfig);
    }
}
