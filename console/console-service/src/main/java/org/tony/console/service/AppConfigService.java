package org.tony.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import org.tony.console.service.model.AppCompareConfigDO;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/1 20:49
 */
public interface AppConfigService {

    /**
     * 查询每日回归配置
     * @param appName
     * @return
     */
    public AppDailyTestConfigDTO queryDailyTest(String appName);


    /**
     * 保存每日回归配置
     * @param appDailyTestConfigDTO
     */
    public void setDailyTest(String appName, AppDailyTestConfigDTO appDailyTestConfigDTO);

    public Map<String, AppDailyTestConfigDTO> queryDailyTestConfig();

    /**
     * 查询测试回归集合
     * @param appName
     * @param env
     * @return
     */
    public AppTestTaskSetDTO queryTestTaskSet(String appName, Env env);

    /**
     * 更新测试回归集合
     * @param appName
     * @param env
     * @param appTestTaskSetDTO
     */
    public void saveTestTaskSet(String appName, Env env, AppTestTaskSetDTO appTestTaskSetDTO);

    /**
     * 查询比对配置
     * @param appName
     * @return
     */
    public AppCompareConfigDO queryCompareConfig(String appName);

    /**
     * 保存比较配置
     * @param appCompareConfigDO
     */
    public void saveOrUpdateCompareConfig(String appName, AppCompareConfigDO appCompareConfigDO);

    /**
     * 查询动态配置
     * @param appName
     * @param env
     * @return
     */
    public DynamicConfig queryDynamicConfig(String appName, String env);

    /**
     * 保存动态配置
     * @param appName
     * @param dynamicConfig
     */
    public void saveOrUpdateDynamicConfig(String appName, String env, DynamicConfig dynamicConfig);
}
