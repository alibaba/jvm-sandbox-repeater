package org.tony.console.biz.event;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.model.event.PushAgentEvent;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleInfoService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/23 14:44
 */
@Slf4j
@Component
public class PushAgentEventListener {
    @Value("${repeat.dynamic.reload.url}")
    private String reloadDynamicURI;

    @Value("${repeat.static.reload.url}")
    private String reloadStaticURI;

    @Value("${repeat.groovy.reload.url}")
    private String reloadGroovyURI;

    @Resource
    ModuleInfoService moduleInfoService;

    @Async
    @EventListener(PushAgentEvent.class)
    public void execute(PushAgentEvent event) {
        ModuleInfoQuery moduleInfoQuery = new ModuleInfoQuery();
        moduleInfoQuery.setAppName(event.getAppName());
        moduleInfoQuery.setEnvironment(event.getEnv().toLowerCase());
        List<ModuleInfoBO> moduleInfoBOList = moduleInfoService.queryV2(moduleInfoQuery);
        if (CollectionUtils.isEmpty(moduleInfoBOList)) {
            return;
        }

        switch (event.getType()) {
            case 0:
                pushStaticConfig(moduleInfoBOList);
                return;
            case 1:
                pushDynamicConfig(moduleInfoBOList);
            case 2:
                pushGroovyConfig(moduleInfoBOList, event.getGroovyId());
                return;
        }
    }

    public void pushStaticConfig(List<ModuleInfoBO> moduleInfoBOList) {
        for (ModuleInfoBO m : moduleInfoBOList) {
            if (!ModuleStatus.ACTIVE.equals(m.getStatus())) {
                continue;
            }
            HttpUtil.Resp resp1 = HttpUtil.doGet(String.format(reloadStaticURI, m.getIp(), m.getPort()), 1);
            if (resp1.isSuccess()) {
                log.info("刷新静态配置成功 appName={} ip={} env={}", m.getAppName(), m.getIp(), m.getEnvironment());
            }

        }
    }

    public void pushDynamicConfig(List<ModuleInfoBO> moduleInfoBOList) {
        for (ModuleInfoBO m : moduleInfoBOList) {
            if (!ModuleStatus.ACTIVE.equals(m.getStatus())) {
                continue;
            }
            HttpUtil.Resp resp1 = HttpUtil.doGet(String.format(reloadDynamicURI, m.getIp(), m.getPort()), 1);
            if (resp1.isSuccess()) {
                log.info("刷新动态配置成功 appName={} ip={} env={}", m.getAppName(), m.getIp(), m.getEnvironment());
            }

        }
    }

    public void pushGroovyConfig(List<ModuleInfoBO> moduleInfoBOList, Long groovyId) {
        for (ModuleInfoBO m : moduleInfoBOList) {
            if (!ModuleStatus.ACTIVE.equals(m.getStatus())) {
                continue;
            }
            HttpUtil.Resp resp1 = HttpUtil.doGet(String.format(reloadGroovyURI, m.getIp(), m.getPort(), groovyId), 1);
            if (resp1.isSuccess()) {
                log.info("刷新groovy成功 appName={} ip={} env={} id={}", m.getAppName(), m.getIp(), m.getEnvironment(), groovyId);
            }
        }
    }
}
