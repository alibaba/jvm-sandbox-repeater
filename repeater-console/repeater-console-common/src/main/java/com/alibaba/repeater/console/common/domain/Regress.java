package com.alibaba.repeater.console.common.domain;


/**
 * {@link Regress} 回放示例
 * <p>
 *
 * @author zhaoyb1990
 */
public class Regress implements java.io.Serializable {
    private Long timestamp;
    private String name;
    private Integer index;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
