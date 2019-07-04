package com.alibaba.jvm.sandbox.repeater.module.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * {@link LogbackUtils}
 * <p>
 *
 * @author zhaoyb1990
 */
public class LogbackUtils {

    /**
     * 初始化日志框架
     *
     * @param logbackCfgFilePath 配置文件绝对路径
     */
    public static void init(String logbackCfgFilePath) {
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        final JoranConfigurator configurator = new JoranConfigurator();
        final File configureFile = new File(logbackCfgFilePath);
        configurator.setContext(loggerContext);
        loggerContext.reset();
        InputStream is = null;
        final Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
        try {
            is = new FileInputStream(configureFile);
            configurator.doConfigure(is);
            logger.info("initializing logback success. file={};", configureFile);
        } catch (Throwable cause) {
            logger.warn("initialize logback failed. file={};", configureFile, cause);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 关闭日志框架
     */
    public static void destroy() {
        try {
            ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
        } catch (Throwable cause) {
            cause.printStackTrace();
        }
    }
}
