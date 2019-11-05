package com.alibaba.jvm.sandbox.repeater.plugin.core.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.ExceptionAware;


import static com.alibaba.jvm.sandbox.repeater.plugin.core.util.PropertyUtil.getSystemPropertyOrDefault;

/**
 * {@link ApplicationModel} 描述一个基础应用模型
 * <p>
 * 应用名    {@link ApplicationModel#appName}
 * 机器名    {@link ApplicationModel#host}
 * 环境信息  {@link ApplicationModel#environment}
 * 模块配置  {@link ApplicationModel#config}
 * </p>
 *
 * @author zhaoyb1990
 */
public class ApplicationModel {

    private String appName;

    private String environment;

    private String host;

    private volatile RepeaterConfig config;

    private ExceptionAware ea = new ExceptionAware();

    private volatile boolean fusing = false;

    private static ApplicationModel instance = new ApplicationModel();

    private ApplicationModel() {
        // for example, you can define it your self
        this.appName = getSystemPropertyOrDefault("app.name", "unknown");
        this.environment = getSystemPropertyOrDefault("app.env", "unknown");
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // default value for disaster
            this.host = "127.0.0.1";
        }
    }

    public static ApplicationModel instance() {
        return instance;
    }

    /**
     * 是否正在工作（熔断机制）
     *
     * @return true/false
     */
    public boolean isWorkingOn() {
        return !fusing;
    }

    /**
     * 是否降级（系统行为）
     *
     * @return true/false
     */
    public boolean isDegrade() {
        return config == null || config.isDegrade();
    }

    /**
     * 异常阈值检测
     *
     * @param throwable 异常类型
     */
    public void exceptionOverflow(Throwable throwable) {
        if (ea.exceptionOverflow(throwable, config == null ? 1000 : config.getExceptionThreshold())) {
            fusing = true;
            ea.printErrorLog();
        }
    }

    public Integer getSampleRate(){
        return config == null ? 0 : config.getSampleRate();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public RepeaterConfig getConfig() {
        return config;
    }

    public void setConfig(RepeaterConfig config) {
        this.config = config;
    }

    public ExceptionAware getEa() {
        return ea;
    }

    public void setEa(ExceptionAware ea) {
        this.ea = ea;
    }

    public boolean isFusing() {
        return fusing;
    }

    public void setFusing(boolean fusing) {
        this.fusing = fusing;
    }
}
