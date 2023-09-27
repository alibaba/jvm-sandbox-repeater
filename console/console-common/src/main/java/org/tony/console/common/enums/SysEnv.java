package org.tony.console.common.enums;

/**
 * @author peng.hu1
 * @Date 2023/5/11 09:44
 */
public enum SysEnv {

    TEST,DEV,CN_STG,CN_PROD,EU_STG,EU_PROD;

    public static SysEnv fromString(String v) {
        if (v==null) {
            return null;
        }

        return SysEnv.valueOf(v.toUpperCase());
    }

}
