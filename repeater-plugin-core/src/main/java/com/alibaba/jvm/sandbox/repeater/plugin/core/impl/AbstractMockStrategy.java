package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse.Action;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AbstractMockStrategy}抽象的mock策略执行；子类只需要实现{@code select}方法即可
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractMockStrategy implements MockStrategy {

    protected final static Logger log = LoggerFactory.getLogger(AbstractMockStrategy.class);

    /**
     * 选择出回放的invocation
     *
     * @param request mock回放请求
     * @return 选择结果
     */
    protected abstract SelectResult select(final MockRequest request);

    /**
     * 子调用找不到情况下的处理类
     * @param request
     * @return
     */
    protected MockResponse executeWithOutInvocation(final MockRequest request) {
        return null;
    }

    @Override
    public MockResponse execute(final MockRequest request) {
        MockResponse response;
        try {
            /*
             * before select hook;
             */
            MockInterceptorFacade.instance().beforeSelect(request);
            /*
             * do select
             */
            SelectResult select = select(request);

            MockInvocation mi = new MockInvocation();
            mi.setIndex(SequenceGenerator.generate(request.getTraceId() + "#"));
            mi.setCurrentUri(request.getIdentity().getUri());
            mi.setCurrentArgs(request.getArgumentArray());
            mi.setTraceId(request.getTraceId());
            mi.setCost(select.getCost());
            mi.setRepeatId(request.getRepeatId());
            // add mock invocation
            RepeatCache.addMockInvocation(mi);

            Invocation invocation = select.getInvocation();
            // matching success
            if (select.isMatch() && invocation != null) {
                response = MockResponse.builder()
                        .action(invocation.getThrowable() == null ? Action.RETURN_IMMEDIATELY : Action.THROWS_IMMEDIATELY)
                        .throwable(invocation.getThrowable())
                        .invocation(invocation)
                        .build();
                mi.setSuccess(true);
                mi.setOriginUri(invocation.getIdentity().getUri());
                mi.setOriginArgs(invocation.getRequest());
                mi.setOriginIndex(invocation.getIndex());

                //这里设置序列化之后的，用于子调用替换逻辑
                Serializer serializer = SerializerWrapper.getSerializer(invocation.getSerializeType());
                mi.setCurrentRequestSerialized(serializer.serialize2String(mi.getCurrentArgs(), request.getEvent().javaClassLoader));
            } else {
                //子调用找不到处理类
                response = executeWithOutInvocation(request);
                if (response != null) {
                    if (Action.SKIP_IMMEDIATELY.equals( response.getAction())) {
                        mi.setSkip(true);
                        mi.setSuccess(false);
                        mi.setOriginIndex(1);
                        mi.setOriginUri(request.getIdentity().getUri());
                        mi.setOriginArgs(request.getArgumentArray());
                    }

                    if (Action.RETURN_IMMEDIATELY.equals( response.getAction())) {
                        mi.setSkip(false);
                        mi.setSuccess(true);
                        mi.setOriginIndex(1);
                        mi.setOriginUri(request.getIdentity().getUri());
                        mi.setOriginArgs(request.getArgumentArray());
                    }

                    return response;
                }

                response = MockResponse.builder()
                        .action(Action.THROWS_IMMEDIATELY)
                        .throwable(new RepeatException("no matching invocation found" + request.getIdentity().getUri()))
                        .isInvocationNotFund(true)
                        .build();
            }
            /*
             * before return hook;
             */
            MockInterceptorFacade.instance().beforeReturn(request, response);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-uncaught exception occurred when execute mock strategy, type={}", type(), throwable);
            response = MockResponse.builder().
                    action(Action.THROWS_IMMEDIATELY)
                    .throwable(throwable)
                    .build();
        }
        return response;
    }
}
