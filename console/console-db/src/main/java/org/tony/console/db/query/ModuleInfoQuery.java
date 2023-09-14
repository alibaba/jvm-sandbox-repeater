package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
public class ModuleInfoQuery  extends BaseQuery {

    private String appName;

    private String ip;

    private String environment;

    public ModuleInfoQuery() {}

    public ModuleInfoQuery(String appName, String ip, String environment) {
        this.appName = appName;
        this.ip = ip;
        this.environment = environment;
    }

    @Override
    public void parseParams(Map<String, Object> params) {
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }

        if (StringUtils.isNotEmpty(ip)) {
            params.put("ip", ip);
        }

        if (StringUtils.isNotEmpty(environment)) {
            params.put("environment", environment);
        }
    }
}
