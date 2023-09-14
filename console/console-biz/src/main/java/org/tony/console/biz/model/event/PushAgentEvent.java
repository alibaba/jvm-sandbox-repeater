package org.tony.console.biz.model.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/2/23 14:40
 */
@Builder
@Data
public class PushAgentEvent {

    /**
     * 应用名
     */
    private String appName;

    /**
     * 环境
     */
    private String env;

    /**
     * 0 静态配置
     * 1 动态配置
     * 2 groovy content更新/创建
     * 3 groovy 删除
     */
    private int type;

    /**
     * groovy配置的id
     */
    private Long groovyId;
}
