package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.api.event.InvokeEvent;

/**
 * {@link InvocationFilter} 调用过滤器，抽象给用户去过滤请求，不用重复去实现EventListener
 * <p>
 * 由于DefaultEventListener中抽象的逻辑较多，对于部分用户要去重新实现该监听器可能会导致功能异常；
 * 因为预留filter接口用于过滤掉部分请求（例如：对于dubbo这种异步请求，需要拦截onResponse和invoke两个方法做不同事情时，可以通过filter来实现）
 * </p>
 *
 * @author zhaoyb1990
 */
public interface InvocationFilter {

    /**
     * 是否忽略当前事件
     *
     * @param event 事件
     * @return true / false
     */
    boolean ignoreEvent(InvokeEvent event);
}
