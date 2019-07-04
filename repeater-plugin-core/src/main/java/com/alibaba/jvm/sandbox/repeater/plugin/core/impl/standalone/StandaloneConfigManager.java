package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.api.ConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PathUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * {@link StandaloneConfigManager} 本地的配置管理
 * <p>
 *
 * @author zhaoyb1990
 */
public class StandaloneConfigManager implements ConfigManager {

    private final static Logger log = LoggerFactory.getLogger(StandaloneConfigManager.class);

    @Override
    public RepeaterResult<RepeaterConfig> pullConfig() {
        String localConfigPath = PathUtils.getConfigPath() + "/repeater-config.json";
        try {
            String config = FileUtils.readFileToString(new File(localConfigPath), "UTF-8");
            RepeaterConfig rc = JSON.parseObject(config, RepeaterConfig.class);
            return RepeaterResult.builder().success(true).data(rc).message("operate success").build();
        } catch (Exception e) {
            log.error("error occurred when pull local config", e);
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        }
    }

}
