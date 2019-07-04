package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;

/**
 * {@link Repeater} 回放处理器
 * <p>
 * 回放器作为流量发起放，需要将{@link RecordModel#entranceInvocation}调用转化为一次请求发起调用；
 * 每种类型{@link InvokeType}的入口流量需要自定义回放器，否则无法进行流量回放
 * </p>
 *
 * @author zhaoyb1990
 */
public interface Repeater {

    /**
     * 开始回放
     *
     * @param context 回放上下文
     * @see RepeatContext
     * @see RecordModel
     * @see RepeatMeta
     */
    void repeat(RepeatContext context);

    /**
     * 流量回放器类型
     *
     * @return 回放器类型
     * @see com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType
     */
    InvokeType getType();

    /**
     * 回放器唯一标志
     *
     * @return 标志
     */
    String identity();

    /**
     * 是否生效
     *
     * @param config 配置文件
     * @return true/false
     */
    boolean enable(RepeaterConfig config);

    /**
     * 设置广播
     *
     * @param broadcast 消息广播
     */
    void setBroadcast(Broadcaster broadcast);
}
