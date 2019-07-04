package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;

/**
 * {@link MockInterceptor} Mock策略执行中的拦截器；给用户自定义mock过程干预的机会
 * <p>
 *
 * @author zhaoyb1990
 */
public interface MockInterceptor {

    /**
     * 在进行子调用选择之前
     *
     * 一般用于一些常见请求参数中一些确定会变的特殊参数处理；
     *
     * @param request mock请求对象
     */
    void beforeSelect(MockRequest request);

    /**
     * 在执行mock返回之前
     *
     * 一般用于修改子调用response中的一些属性
     *
     * @param request mock请求对象
     * @param response mock返回结果
     */
    void beforeReturn(MockRequest request, MockResponse response);

    /**
     * 是否命中该条拦截器的select规则
     *
     * @param request mock请求对象
     * @return 是否命中
     */
    boolean matchingSelect(MockRequest request);

    /**
     * 是否命中该条拦截器的return规则
     *
     * @param request  mock请求对象
     * @param response mock返回结果
     * @return 是否命中
     */
    boolean matchingReturn(MockRequest request, MockResponse response);
}
