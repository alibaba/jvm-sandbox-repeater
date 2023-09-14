package com.alibaba.jvm.sandbox.repeater.plugin.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.IpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
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

    private volatile DynamicConfig dynamicConfig;

    private ExceptionAware ea = new ExceptionAware();

    private volatile boolean fusing = false;

    private static ApplicationModel instance = new ApplicationModel();

    private static Filter autoTypeFilter;

    private ApplicationModel() {
        // for example, you can define it your self
        this.appName = getSystemPropertyOrDefault("app.name", "unknown");
        this.environment = getSystemPropertyOrDefault("app.env", "unknown");
        this.host = IpUtil.getLocalIp();
    }

    public static ApplicationModel instance() {
        return instance;
    }

    public static Filter getAutoTypeFilter() {
        if (autoTypeFilter!=null) {
            return autoTypeFilter;
        }

        if (instance==null||instance.getConfig()==null) {
            autoTypeFilter = JSONReader.autoTypeFilter(
                    // 按需加上需要支持自动类型的类名前缀，范围越小越安全
                    "com.",
                    "org.",
                    "java."
            );
        } else {
            String[] autoTypes = instance.getConfig().getAutoTypes();
            List<String> configs = new ArrayList<>();
            configs.add("com.");
            configs.add("org.");
            if (autoTypes!=null && autoTypes.length>0) {
                configs.addAll(Arrays.asList(autoTypes));
            }
            String[] a = new String[configs.size()];
            configs.toArray(a);
            autoTypeFilter = JSONReader.autoTypeFilter(
                   a
            );
        }

        return autoTypeFilter;
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

    public DynamicConfig getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(DynamicConfig dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }
}
