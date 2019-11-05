package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy.StrategyType;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RepeatMeta} 回放配置元数据
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeatMeta implements java.io.Serializable {

    /**
     * 回放应用名
     */
    private String appName;
    /**
     * 回放traceId
     */
    private String traceId;
    /**
     * 是否启用mock；启用后子调用不发生真实调用
     */
    private boolean mock;
    /**
     * 子调用匹配查找策略，开启mock回放后生效
     */
    private StrategyType strategyType;
    /**
     * 回放ID
     */
    private String repeatId;
    /**
     * 相似度对比，具体参见{@link StrategyType#PARAMETER_MATCH}
     */
    private double matchPercentage = 100;
    /**
     * 回放数据源；服务端可指定module从何处加载回放数据源；http接口
     */
    private String datasource;

    /**
     * 调用超时时间
     */
    private Integer timeout = 30000;

    private Map<String,String> extension = new HashMap<String, String>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
