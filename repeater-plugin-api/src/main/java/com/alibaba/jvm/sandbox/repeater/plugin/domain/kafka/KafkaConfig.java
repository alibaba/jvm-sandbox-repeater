package com.alibaba.jvm.sandbox.repeater.plugin.domain.kafka;

/**
 * @author peng.hu1
 * @Date 2022/12/6 17:55
 */
public class KafkaConfig {

    /**
     * 服务地址
     */
    private String server;

    /**
     * 重试次数
     */
    private int retries;

    /**
     * 采集topic
     */
    private String recordTopic;

    /**
     * 回放topic
     */
    private String repeatTopic;

    private String username;

    private String password;

    public String getServer() {

        if (server!=null) {
            return server;
        }

        return "common-kafka-test.nioint.com:39094";
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getUsername() {
        if (username!=null) {
            return this.username;
        }
        return "feiTjL4l1Dif";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (password!=null) {
            return this.password;
        }
        return "TPdL4aPOnk3O";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecordTopic() {
        if (recordTopic!=null) {
            return recordTopic;
        }

        return "nio_repeator_topic";
    }

    public void setRecordTopic(String recordTopic) {
        this.recordTopic = recordTopic;
    }

    public String getRepeatTopic() {
        if (repeatTopic!=null) {
            return repeatTopic;
        }

        return "nio_repeator_repeat_topic";
    }

    public void setRepeatTopic(String repeatTopic) {
        this.repeatTopic = repeatTopic;
    }
}
