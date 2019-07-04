package com.alibaba.repeater.console.dal.model;


import java.util.Date;

/**
 * {@link Record}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Record implements java.io.Serializable{

    private Long id;

    private Date gmtCreate;

    private Date gmtRecord;

    private String appName;

    private String environment;

    private String host;

    private String traceId;

    private String wrapperRecord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtRecord() {
        return gmtRecord;
    }

    public void setGmtRecord(Date gmtRecord) {
        this.gmtRecord = gmtRecord;
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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getWrapperRecord() {
        return wrapperRecord;
    }

    public void setWrapperRecord(String wrapperRecord) {
        this.wrapperRecord = wrapperRecord;
    }
}
