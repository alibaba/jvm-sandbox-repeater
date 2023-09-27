package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author peng.hu1
 * @Date 2022/12/12 12:11
 */
public class DynamicConfig {

    private String logLevel;

    /**
     * skip mock； 子调用找不到的情况下，放开mock
     */
    private Set<String> skipMockIdentities;

    /**
     * 一律放开, 即便有mock的子调用也不mock
     */
    private Set<String> skipMockIdentities2;

    public DynamicConfig() {
        this.logLevel = "INFO";
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Set<String> getSkipMockIdentities() {
        if (skipMockIdentities == null) {
            skipMockIdentities = new HashSet<>();
        }

        return skipMockIdentities;
    }

    public void setSkipMockIdentities(Set<String> skipMockIdentities) {
        this.skipMockIdentities = skipMockIdentities;
    }

    public Set<String> getSkipMockIdentities2() {
        if (skipMockIdentities2 == null) {
            skipMockIdentities2 = new HashSet<>();
        }
        return skipMockIdentities2;
    }

    public void setSkipMockIdentities2(Set<String> skipMockIdentities2) {
        this.skipMockIdentities2 = skipMockIdentities2;
    }
}
