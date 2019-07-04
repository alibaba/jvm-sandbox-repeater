package com.alibaba.jvm.sandbox.repeater.plugin.api;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.SubscribeSupporter;

/**
 *
 * <p>
 *
 * @author zhaoyb1990
 */
public interface LifecycleManager {

    /**
     * 加载调用插件SPI
     *
     * @return 插件列表
     * @see InvokePlugin
     */
    List<InvokePlugin> loadInvokePlugins();

    /**
     * 加载回放器SPI
     *
     * @return 回放器列表
     * @see Repeater
     */
    List<Repeater> loadRepeaters();

    /**
     * 加载事件订阅支持SPI
     *
     * @return 事件
     * @see SubscribeSupporter
     * @see com.google.common.eventbus.EventBus
     */
    List<SubscribeSupporter> loadSubscribes();

    /**
     * 释放资源
     */
    void release();
}
