package org.tony.console.biz.model;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/2/21 09:32
 */
@Data
public class StaticConfigVO {

    /**
     * 全局采样率
     */
    private Integer sampleRate = 10000;

    /**
     * 开启ttl异步线程
     */
    private Boolean useTtl;

    /**
     * 录制降级开关
     */
    private boolean degrade = false;

    /**
     * 延迟注入时间
     */
    private Integer delayTime = 10;

    /**
     * 注入类型
     */
    private String serializeType;

    /**
     * http采样配置
     */
    private Map<String, Long> httpEntrancePatternsWithSampleRate = Maps.newHashMap();

    /**
     * java入口插件动态增强的行为
     */
    private List<BehaviorVO> mainInvokes = Lists.newArrayList();

    /**
     * java子调用插件动态增强的行为
     */
    private List<BehaviorVO> javaSubInvokes = Lists.newArrayList();

    /**
     * 插件的情况
     */
    private List<PluginConfigVO> pluginConfigVOS;
}
