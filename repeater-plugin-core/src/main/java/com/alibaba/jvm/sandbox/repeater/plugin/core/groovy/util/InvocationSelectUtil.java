package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.util;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.StrategyProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.ParameterMatchMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

/**
 * @author peng.hu1
 * @Date 2023/4/23 16:08
 */
public class InvocationSelectUtil {

    public static SelectResult select(MockRequest mockRequest) {
        ParameterMatchMockStrategy strategy = (ParameterMatchMockStrategy) StrategyProvider.instance().provide(MockStrategy.StrategyType.PARAMETER_MATCH);
        return strategy.select(mockRequest);
    }
}
