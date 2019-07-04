package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy.StrategyType;

/**
 * 回放策略单例实现；根据策略类型提供{@link MockStrategy}
 *
 * <p>
 * 该SPI loader基于 repeater-plugin-core，只能识别repeater-plugin-core中的实现
 * </p>
 *
 * @author zhaoyb1990
 */
class StrategyProvider {

    private final static StrategyProvider INSTANCE = new StrategyProvider();

    public static StrategyProvider instance() {
        return INSTANCE;
    }

    private final Map<StrategyType, MockStrategy> strategyCached = new ConcurrentHashMap<StrategyType, MockStrategy>(2);

    private StrategyProvider() {
        ServiceLoader<MockStrategy> strategies = ServiceLoader.load(MockStrategy.class, this.getClass().getClassLoader());
        for (MockStrategy strategy : strategies) {
            strategyCached.put(strategy.type(), strategy);
        }
    }

    MockStrategy provide(StrategyType type) {
        final MockStrategy strategy = strategyCached.get(type);
        return strategy == null ? strategyCached.get(StrategyType.DEFAULT) : strategy;
    }

}
