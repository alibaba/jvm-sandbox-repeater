package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;

/**
 * {@link InvocationListener} 调用监听器
 * <p>
 * 录制回放核心处理逻辑；
 *
 * 当插件组装完成一次invocation之后，需要通过{@link InvocationListener}回调，{@link InvocationListener}需要根据调用类型分类处理
 *
 * 根据{@link Invocation#entrance}区分:
 *
 * 非入口{@link Invocation#entrance}==false执行
 * {@link RecordModel#subInvocations}组装；
 * 入口  {@link Invocation#entrance}==true执行
 * {@link RecordModel#entranceInvocation}组装；并通过{@link Broadcaster}分发消息
 * </p>
 *
 * @author zhaoyb1990
 */
public interface InvocationListener {

    /**
     * invocation回调逻辑
     *
     * @param invocation 组装的调用信息
     * @see Invocation
     */
    void onInvocation(final Invocation invocation);
}
