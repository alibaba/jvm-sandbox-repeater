package org.tony.console.service.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.enums.SysEnv;

/**
 * @author peng.hu1
 * @Date 2023/5/11 09:42
 */
@Component
public class EnvConfig implements InitializingBean {

    @Value("${system.env}")
    private String env;

    static SysEnv currentEnv;

    static {
        currentEnv = SysEnv.TEST;
    }

    public SysEnv getCurrentEnv() {
        return currentEnv;
    }

    public boolean isEuStg() {

        if (currentEnv.equals(SysEnv.EU_STG)) {
            return true;
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotEmpty(env)) {
            currentEnv = SysEnv.fromString(env);
        }
    }
}
