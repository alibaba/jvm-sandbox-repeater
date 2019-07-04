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

    private String appName;

    private String traceId;

    private boolean mock;

    private StrategyType strategyType;

    private String repeatId;

    private double matchPercentage = 100;

    private String datasource;

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
}
