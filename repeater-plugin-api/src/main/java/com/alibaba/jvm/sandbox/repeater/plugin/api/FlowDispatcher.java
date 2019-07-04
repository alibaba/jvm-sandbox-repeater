package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;

/**
 * {@link FlowDispatcher} 流量分配器，用于分配回放流量到指定的回放器
 * <p>
 *
 * @author zhaoyb1990
 * @since 1.0.0
 */
public interface FlowDispatcher {

    /**
     * 分发流量
     *
     * @param meta   回放元数据
     * @param recordModel 录制的调用记录
     * @exception RepeatException 回放异常
     */
    void dispatch(RepeatMeta meta, RecordModel recordModel) throws RepeatException;
}
