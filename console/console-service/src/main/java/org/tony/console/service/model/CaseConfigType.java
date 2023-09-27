package org.tony.console.service.model;

/**
 * @author peng.hu1
 * @Date 2023/9/5 10:44
 */
public enum CaseConfigType {

    SORT_CONFIG(0), //排序配置
    IGNORE_COMPARE_CONFIG(1); //忽略比对配置

    public int code;

    CaseConfigType(int code) {
        this.code = code;
    }

    public static CaseConfigType byCode(int code) {
        for (CaseConfigType configType : CaseConfigType.values()) {
            if (configType.code == code) {
                return configType;
            }
        }

        return null;
    }
}
