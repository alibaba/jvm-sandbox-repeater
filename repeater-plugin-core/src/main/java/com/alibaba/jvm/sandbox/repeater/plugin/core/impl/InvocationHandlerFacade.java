package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvocationHandler;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author peng.hu1
 * @Date 2023/4/23 15:42
 */
public class InvocationHandlerFacade implements InvocationHandler {

    private static final InvocationHandlerFacade INSTANCE = new InvocationHandlerFacade();

    private List<InvocationHandler> handlers = Lists.newArrayList();

    private InvocationHandlerFacade() {
        ServiceLoader<InvocationHandler> sl = ServiceLoader.load(InvocationHandler.class, this.getClass().getClassLoader());
        final Iterator<InvocationHandler> iterator = sl.iterator();
        while (iterator.hasNext()) {
            handlers.add(iterator.next());
        }
    }

    public static InvocationHandlerFacade instance() {
        return INSTANCE;
    }

    @Override
    public MockResponse executeNotFundInvocation(MockRequest request) {
        for (InvocationHandler handler : handlers) {
            if (handler.matchingNotFund(request)) {
                return handler.executeNotFundInvocation(request);
            }
        }

        return null;
    }

    @Override
    public boolean matchingNotFund(MockRequest request) {
        return true;
    }
}
