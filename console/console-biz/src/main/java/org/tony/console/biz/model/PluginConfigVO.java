package org.tony.console.biz.model;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/2/21 10:03
 */
@Data
public class PluginConfigVO {

    /**
     * 是否开启
     */
    private Boolean open;

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件id
     */
    private String identity;
}
