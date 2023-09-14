package org.tony.console.common.domain;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/1/30 14:56
 */
@Data
public class Tag {

    /**
     * 标签的id
     */
    private String id;

    /**
     * 标签的名称
     */
    private String label;

    /**
     * 标签的值
     */
    private String value;


    public Tag() {}

    public Tag(String id, String label, String value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }
}
