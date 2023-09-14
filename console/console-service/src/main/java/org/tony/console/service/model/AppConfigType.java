package org.tony.console.service.model;

/**
 * 配置模板
 */
public enum AppConfigType {

    GLOBAL_COMPARE_CONFIG(0), //全局比对配置
    DYNAMIC_CONFIG(1), //全局比对配置
    TEST_TASK_SET_CONFIG(2), //回归用例集合
    DAILY_TEST_CONFIG(3); //每日回归配置

    public int code;

    AppConfigType(int code) {
        this.code = code;
    }

    public static AppConfigType byCode(int code) {
        for (AppConfigType configType : AppConfigType.values()) {
            if (configType.code == code) {
                return configType;
            }
        }

        return null;
    }
}
