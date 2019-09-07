package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

import org.kohsuke.MetaInfServices;

/**
 * <p>
 * 默认的回放策略，阻断所有的请求
 *
 * @author zhaoyb1990
 */
@MetaInfServices(MockStrategy.class)
public class DefaultMockStrategy extends AbstractMockStrategy {

    @Override
    public SelectResult select(MockRequest request, Boolean removeFromSubInvocationIfFound) {
        log.debug("使用了默认的Mock策略");
        return SelectResult.builder()
                .cost(0L)
                .match(false)
                .build();
    }

    @Override
    public StrategyType type() {
        return StrategyType.DEFAULT;
    }
}
