package com.alibaba.jvm.sandbox.repeater.plugin.domain.kafka;

/**
 * @author peng.hu1
 * @Date 2022/12/6 18:58
 */
public class KafkaMsg<T> {

    public static final int RECORD_TYPE = 1;

    public static final int REPEAT_TYPE = 2;

    public int type;

    public int version;

    public T body;

    public KafkaMsg() {}

    public KafkaMsg(int type, int version, T body) {
        this.type = type;
        this.body = body;
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
