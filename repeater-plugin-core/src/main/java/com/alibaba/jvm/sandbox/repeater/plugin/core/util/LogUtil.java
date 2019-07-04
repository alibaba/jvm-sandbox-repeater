package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LogUtil} 日志类工具；插件没有引入日志框架可以使用{@link LogUtil} 打日志
 * <p>
 *
 * @author zhaoyb1990
 */
public class LogUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);


    public static void info(String placeholder, Object... params) {
        LOGGER.info(placeholder, params);
    }

    public static void error(String placeholder, Object... params) {
        LOGGER.error(placeholder, params);
    }

    public static void warn(String placeholder, Object... params) {
        LOGGER.warn(placeholder, params);
    }

    public static void debug(String placeholder, Object... params) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(placeholder, params);
        }
    }
}
