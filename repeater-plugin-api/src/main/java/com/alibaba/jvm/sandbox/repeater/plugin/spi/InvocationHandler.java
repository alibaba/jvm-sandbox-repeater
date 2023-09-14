package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;

/**
 * 子调用找不到处理类
 */
public interface InvocationHandler {

    /**
     * 子调用找不到的情况下，可以选择自定义
     * @param request
     * @return
     */
    MockResponse executeNotFundInvocation(final MockRequest request);

    /**
     * 子调用找不到处理类
     * @param request
     * @return
     */
    boolean matchingNotFund(MockRequest request);
}
