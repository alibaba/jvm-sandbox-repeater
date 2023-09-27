package org.tony.console.biz.Impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tony.console.biz.ModuleConfigBizService;
import org.tony.console.biz.model.StaticConfigVO;
import org.tony.console.biz.model.convert.StaticConfigVOConvert;
import org.tony.console.biz.model.event.PushAgentEvent;
import org.tony.console.biz.request.UpdateStaticConfigRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.common.domain.ModuleConfigParams;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.ModuleConfigDao;
import org.tony.console.db.model.ModuleConfig;
import org.tony.console.db.query.ModuleConfigQuery;
import org.tony.console.service.AppAuthService;
import org.tony.console.service.ModuleConfigService;
import org.tony.console.service.utils.JacksonUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/1 19:18
 */
@Slf4j
@Component
public class ModuleConfigBizServiceImpl implements ModuleConfigBizService {

    @Resource
    ModuleConfigDao moduleConfigDao;

    @Resource
    ModuleConfigService moduleConfigService;

    @Resource
    StaticConfigVOConvert staticConfigVOConvert;

    @Autowired
    ApplicationContext applicationContext;

    @Resource
    AppAuthService appAuthService;

    @Override
    public void saveOrUpdate(ModuleConfigBO moduleConfigBO) throws BizException {

        ModuleConfigQuery query = new ModuleConfigQuery();
        query.setEnvironment(moduleConfigBO.getEnvironment());
        query.setAppName(moduleConfigBO.getAppName());
        ModuleConfig config = moduleConfigDao.query(query);
        String configString = null;

        // 这里预处理下
        preCheck(moduleConfigBO.getConfigModel());
        try {
            configString = JacksonUtil.serialize(moduleConfigBO.getConfigModel());
        } catch (SerializeException e) {
            log.error("system error",e);

            throw BizException.build("system error");
        }
        if (config!=null) {
            config.setConfig(configString);
            moduleConfigDao.saveOrUpdate(config);
        } else {
            config = new ModuleConfig();
            config.setEnvironment(moduleConfigBO.getEnvironment());
            config.setAppName(moduleConfigBO.getEnvironment());
            config.setConfig(configString);

            moduleConfigDao.saveOrUpdate(config);
        }
    }

    @Override
    public StaticConfigVO getConfig(String appName, String env) {

        ModuleConfigParams params = new ModuleConfigParams();
        params.setAppName(appName);
        params.setEnvironment(env);
        Result<ModuleConfigBO> result = moduleConfigService.queryWithDefault(params);

        ModuleConfigBO configBO = result.getData();
        RepeaterConfig repeaterConfig = configBO.getConfigModel();

        return staticConfigVOConvert.convert(repeaterConfig);
    }

    @Override
    public void updateStaticConfig(UpdateStaticConfigRequest request) throws BizException {
        request.check();
        appAuthService.checkAuth(request.getAppName(), request.getUser());

        ModuleConfigQuery query = new ModuleConfigQuery();
        query.setEnvironment(request.getEnv());
        query.setAppName(request.getAppName());
        ModuleConfig moduleConfig = moduleConfigDao.query(query);

        RepeaterConfig repeaterConfig = null;
        if (moduleConfig!=null) {
            try {
                repeaterConfig = JacksonUtil.deserialize(moduleConfig.getConfig(), RepeaterConfig.class);
            } catch (SerializeException e) {
                log.error("system error", e);
            }
        } else {
            repeaterConfig = new RepeaterConfig();
        }

        //这里进行模型转换
        staticConfigVOConvert.reconvert(repeaterConfig, request.getConfig());

        String configString = null;
        try {
            configString = JacksonUtil.serialize(repeaterConfig);
        } catch (SerializeException e) {
            log.error("system error",e);

            throw BizException.build("system error");
        }

        if (moduleConfig!=null) {
            moduleConfig.setConfig(configString);
            moduleConfigDao.saveOrUpdate(moduleConfig);
        } else {
            moduleConfig = new ModuleConfig();
            moduleConfig.setEnvironment(request.getEnv());
            moduleConfig.setAppName(request.getAppName());
            moduleConfig.setConfig(configString);

            moduleConfigDao.saveOrUpdate(moduleConfig);
        }

        pushConfigToAgent(request.getAppName(), request.getEnv(), 0);
    }

    @Override
    public void pushConfigToAgent(String appName, String env, int type) throws BizException {
        PushAgentEvent pushAgentEvent = PushAgentEvent
                .builder()
                .appName(appName)
                .env(env)
                .type(type)
                .build();
        applicationContext.publishEvent(pushAgentEvent);
    }

    private void preCheck(RepeaterConfig repeaterConfig) {
        List<Behavior> behaviors = repeaterConfig.getJavaSubInvokeBehaviors();
        for (Behavior behavior : behaviors) {
            if (behavior.getClassPattern().startsWith("java")) {
                behavior.setIncludeBootstrapClasses(true);
            }
        }

        //这里
        repeaterConfig.setUseTtl(false);
        repeaterConfig.getRepeatIdentities().clear();
        repeaterConfig.getRepeatIdentities().add("java");
        repeaterConfig.getRepeatIdentities().add("http");
        repeaterConfig.setExceptionThreshold(1000);
    }
}
