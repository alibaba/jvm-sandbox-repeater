package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.GroovyCache;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvocationHandler;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/4/23 16:01
 */
@MetaInfServices(InvocationHandler.class)
public class GroovyInvocationHandler implements InvocationHandler {

    protected final static Logger log = LoggerFactory.getLogger(GroovyInvocationHandler.class);

    @Override
    public MockResponse executeNotFundInvocation(MockRequest request) {
        Map<Long, InvocationHandler> mockInterceptorMap = GroovyCache.getGroovyInvocationHandlerCache();
        for (Map.Entry<Long, InvocationHandler> item : mockInterceptorMap.entrySet()) {
            try {
                if (item.getValue().matchingNotFund(request)) {
                    return item.getValue().executeNotFundInvocation(request);
                }
            } catch (Exception e) {
                log.error("groovy execute error, id={}", item.getKey(), e);
            }
        }

        return null;
    }

    @Override
    public boolean matchingNotFund(MockRequest request) {
        return true;
    }
}
