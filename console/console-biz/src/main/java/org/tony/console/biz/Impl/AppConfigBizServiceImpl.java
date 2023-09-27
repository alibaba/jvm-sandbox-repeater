package org.tony.console.biz.Impl;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tony.console.biz.AppConfigBizService;
import org.tony.console.biz.model.event.PushAgentEvent;
import org.tony.console.biz.request.app.SaveGroovyContentRequest;
import org.tony.console.common.enums.Status;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.AppAuthService;
import org.tony.console.service.AppGroovyService;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.groovy.GroovyConfigDTO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:40
 */
@Component
public class AppConfigBizServiceImpl implements AppConfigBizService {

    @Resource
    AppGroovyService appGroovyService;

    @Resource
    AppAuthService appAuthService;

    @Resource
    ApplicationContext applicationContext;

    @Override
    public List<GroovyConfig> queryGroovyConfigListForAgent(String appName, String env) {

        List<GroovyConfigDTO> groovyConfigDTOS = appGroovyService.queryList(appName, true);
        return groovyConfigDTOS.stream()
                .filter(item->item.getStatus().equals(Status.VALID) && item.getEnvList().contains(Env.fromString(env)) )
                .map(item->{
                    GroovyConfig config = new GroovyConfig();
                    config.setContent(item.getContent());
                    config.setAppName(item.getAppName());
                    config.setId(item.getId());
                    config.setType(item.getGroovyType());
                    config.setVersion(item.getVersion());
                    config.setValid(item.isValid());

                    return config;
                }).collect(Collectors.toList());
    }

    @Override
    public GroovyConfig queryGroovyConfigForAgent(String appName, Long id) {

        GroovyConfigDTO item = appGroovyService.queryById(id);

        GroovyConfig config = new GroovyConfig();
        config.setContent(item.getContent());
        config.setAppName(item.getAppName());
        config.setId(item.getId());
        config.setType(item.getGroovyType());
        config.setVersion(item.getVersion());
        config.setValid(item.isValid());

        return config;
    }

    @Override
    public List<GroovyConfigDTO> queryGroovyList(String appName) {
        return appGroovyService.queryList(appName, false);
    }

    @Override
    public GroovyConfigDTO queryGroovyById(Long id) {
        return appGroovyService.queryById(id);
    }

    @Override
    public void saveGroovyContent(SaveGroovyContentRequest request) throws BizException {
        GroovyConfigDTO groovyConfigDTO = appGroovyService.queryById(request.getId());
        if (groovyConfigDTO == null) {
            throw BizException.build("不存在");
        }

        if (groovyConfigDTO.getVersion()!=request.getVersion()) {
            throw BizException.build("请刷新页面，获取最新的脚本进行编辑");
        }

        appAuthService.checkAuth(groovyConfigDTO.getAppName(), request.getUser());
        groovyConfigDTO.setContent(request.getContent());
        appGroovyService.updateContent(groovyConfigDTO);

        if (groovyConfigDTO.isValid()) {
            for (Env env : groovyConfigDTO.getEnvList()) {
                PushAgentEvent pushAgentEvent = PushAgentEvent
                        .builder()
                        .appName(groovyConfigDTO.getAppName())
                        .type(2)
                        .env(env.toString())
                        .groovyId(groovyConfigDTO.getId())
                        .build();
                applicationContext.publishEvent(pushAgentEvent);
            }
        }
    }
}
