package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.kafka.KafkaConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * {@link RepeaterConfig} 基础配置项
 * <p>
 * 基础配置从服务端推送到启动的agent或者由agent启动的时候主动去服务端拉取配置；
 * <p>
 * 配置主要包含一些模块的工作模式；插件启动鉴权；采样率等
 * </p>
 *
 * @author zhaoyb1990
 * @since 1.0.0
 */
public class RepeaterConfig implements java.io.Serializable{

    /**
     * 是否开启ttl线程上下文切换
     * <p>
     * 开启之后，才能将并发线程中发生的子调用记录下来，否则无法录制到并发子线程的子调用信息
     * <p>
     * 原理是将住线程的threadLocal拷贝到子线程，执行任务完成后恢复
     *
     * @see com.alibaba.ttl.TransmittableThreadLocal
     */
    private boolean useTtl;

    /**
     * 是否执行录制降级策略
     * <p>
     * 开启之后，不进行录制，只处理回放请求
     * 默认为true
     */
    private boolean degrade = true;

    /**
     * 异常发生阈值；默认1000
     * 当{@code ExceptionAware} 感知到异常次数超过阈值后，会降级模块
     */
    private Integer exceptionThreshold = 1000;

    /**
     * 采样率；最小力度万分之一
     * 10000 代表 100%
     */
    private Integer sampleRate = 10000;

    /**
     * 延迟注入时间
     */
    private Integer delayTime = 0;

    /**
     * 插件地址
     */
    private String pluginsPath;

    /**
     * 由于HTTP接口的量太大（前后端未分离的情况可能还有静态资源）因此必须走白名单匹配模式才录制
     */
    private List<String> httpEntrancePatterns = Lists.newArrayList();

    private Map<String, Long> httpEntrancePatternsWithSampleRate = Maps.newHashMap();

    /**
     * java入口插件动态增强的行为
     */
    private List<Behavior> javaEntranceBehaviors = Lists.newArrayList();

    /**
     * java子调用插件动态增强的行为
     */
    private List<Behavior> javaSubInvokeBehaviors = Lists.newArrayList();

    /**
     * 需要启动的插件
     */
    private List<String> pluginIdentities = Lists.newArrayList();

    /**
     * 回放器插件
     */
    private List<String> repeatIdentities = Lists.newArrayList();

    /**
     * kafka地址
     */
    private KafkaConfig kafkaConfig;

    /**
     * 自定义配置项
     */
    private Map<String, String> selfConfig;

    /**
     * 序列化类型
     */
    private String serializeType;

    private String[] autoTypes;

    public boolean isUseTtl() {
        return useTtl;
    }

    public void setUseTtl(boolean useTtl) {
        this.useTtl = useTtl;
    }

    public boolean isDegrade() {
        return degrade;
    }

    public void setDegrade(boolean degrade) {
        this.degrade = degrade;
    }

    public Integer getExceptionThreshold() {
        return exceptionThreshold;
    }

    public void setExceptionThreshold(Integer exceptionThreshold) {
        this.exceptionThreshold = exceptionThreshold;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getPluginsPath() {
        return pluginsPath;
    }

    public void setPluginsPath(String pluginsPath) {
        this.pluginsPath = pluginsPath;
    }

    public List<String> getHttpEntrancePatterns() {
        return httpEntrancePatterns;
    }

    public void setHttpEntrancePatterns(List<String> httpEntrancePatterns) {
        this.httpEntrancePatterns = httpEntrancePatterns;
    }

    public List<Behavior> getJavaEntranceBehaviors() {
        return javaEntranceBehaviors;
    }

    public void setJavaEntranceBehaviors(List<Behavior> javaEntranceBehaviors) {
        this.javaEntranceBehaviors = javaEntranceBehaviors;
    }

    public List<Behavior> getJavaSubInvokeBehaviors() {
        return javaSubInvokeBehaviors;
    }

    public void setJavaSubInvokeBehaviors(List<Behavior> javaSubInvokeBehaviors) {
        this.javaSubInvokeBehaviors = javaSubInvokeBehaviors;
    }

    public List<String> getPluginIdentities() {
        return pluginIdentities;
    }

    public void setPluginIdentities(List<String> pluginIdentities) {
        this.pluginIdentities = pluginIdentities;
    }

    public List<String> getRepeatIdentities() {
        return repeatIdentities;
    }

    public void setRepeatIdentities(List<String> repeatIdentities) {
        this.repeatIdentities = repeatIdentities;
    }

    @Override
    public String toString() {
        return "{" +
                "sampleRate=" + sampleRate +
                ", plugin=" + pluginIdentities +
                '}';
    }

    public Map<String, Long> getHttpEntrancePatternsWithSampleRate() {
        return httpEntrancePatternsWithSampleRate;
    }

    public void setHttpEntrancePatternsWithSampleRate(Map<String, Long> httpEntrancePatternsWithSampleRate) {
        this.httpEntrancePatternsWithSampleRate = httpEntrancePatternsWithSampleRate;
    }

    public KafkaConfig getKafkaConfig() {
        if (kafkaConfig == null) {
            this.kafkaConfig = new KafkaConfig();
        }
        return kafkaConfig;
    }

    public void setKafkaConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public Map<String, String> getSelfConfig() {
        return selfConfig;
    }

    public void setSelfConfig(Map<String, String> selfConfig) {
        this.selfConfig = selfConfig;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    public String getSerializeType() {
        if (serializeType==null) {
            serializeType = "HESSIAN";
        }
        return serializeType;
    }

    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String[] getAutoTypes() {
        return autoTypes;
    }

    public void setAutoTypes(String[] autoTypes) {
        this.autoTypes = autoTypes;
    }
}
