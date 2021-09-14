package com.alibaba.jvm.sandbox.repeater.plugin.utils;

import java.util.Objects;

/**
 * UriMatchUtils
 *
 * @author vivo-钱兆良
 * @version 1.0
 * @date 2020/12/15 17:40
 */
public class UriMatchUtils {

    public static boolean match(String[] uriItemArray, String targetUri) {
        String[] uriArr = targetUri.split("/");
        if (uriItemArray.length != uriArr.length) {
            return false;
        }

        int len = uriItemArray.length;
        for (int i = 0; i < len; i++) {
            if (uriItemArray[i].startsWith("{") && uriItemArray[i].endsWith("}")) {
                continue;
            }
            if (Objects.equals(uriItemArray[i], uriArr[i])) {
                continue;
            }
            return false;
        }
        return true;
    }
}