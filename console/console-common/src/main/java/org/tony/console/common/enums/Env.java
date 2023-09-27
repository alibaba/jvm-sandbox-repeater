package org.tony.console.common.enums;

/**
 * @author peng.hu1
 * @Date 2023/2/28 19:26
 */
public enum Env {
    TEST,STG,DEV,PROD,ALL;

    public static Env fromString(String v) {
        if (v==null) {
            return null;
        }

        return Env.valueOf(v.toUpperCase());
    }
}
