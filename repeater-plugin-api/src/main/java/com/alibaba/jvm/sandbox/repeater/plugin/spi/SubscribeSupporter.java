package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.SubscribeEvent;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public interface SubscribeSupporter<E extends SubscribeEvent> {

    /**
     * 开始注册当前订阅到{@link com.google.common.eventbus.EventBus}
     */
    void register();

    /**
     * 取消注册当前订阅
     */
    void unRegister();

    /**
     * 订阅类型
     *
     * @return 类型
     */
    String type();

    /**
     * 接收订阅事件处理
     * <p>
     * 实现类需要加上{@link Subscribe}注解 和{@link AllowConcurrentEvents}(如果要支持并行)
     */
    void onSubscribe(E event);
}
