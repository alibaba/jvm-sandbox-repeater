package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:53
 */
public class GroovyConfig {

    private Long id;

    private GroovyType type;

    private String content;

    private int version;

    private String appName;

    private boolean valid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroovyType getType() {
        return type;
    }

    public void setType(GroovyType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
