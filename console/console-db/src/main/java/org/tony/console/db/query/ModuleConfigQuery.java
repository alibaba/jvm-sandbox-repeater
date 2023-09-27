package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class ModuleConfigQuery extends BaseQuery{

    private String appName;

    private String environment;

    private String config;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }

        if (StringUtils.isNotEmpty(environment)) {
            params.put("environment", environment);
        }

        if (StringUtils.isNotEmpty(config)) {
            params.put("config", config);
        }
    }
}
