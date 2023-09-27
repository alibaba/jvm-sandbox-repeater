package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.GroovyCache;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:44
 */
@MetaInfServices(MockInterceptor.class)
public class GroovyMockInterceptor implements MockInterceptor {

    protected final static Logger log = LoggerFactory.getLogger(GroovyMockInterceptor.class);

    @Override
    public void beforeSelect(MockRequest request) {
        Map<Long, MockInterceptor> mockInterceptorMap = GroovyCache.getMockStrategyMap();
        for (Map.Entry<Long, MockInterceptor> item : mockInterceptorMap.entrySet()) {
            try {
                if (item.getValue().matchingSelect(request)) {
                    item.getValue().beforeSelect(request);
                }
            } catch (Exception e) {
                log.error("groovy execute error, id={}", item.getKey(), e);
            }
        }
    }

    @Override
    public void beforeReturn(MockRequest request, MockResponse response) {
        Map<Long, MockInterceptor> mockInterceptorMap = GroovyCache.getMockStrategyMap();
        for (Map.Entry<Long, MockInterceptor> item : mockInterceptorMap.entrySet()) {
            try {
                if (item.getValue().matchingReturn(request, response)) {
                    item.getValue().beforeReturn(request, response);
                }
            } catch (Exception e) {
                log.error("groovy execute error, id={}", item.getKey(), e);
            }
        }
    }

    @Override
    public boolean matchingSelect(MockRequest request) {
        return true;
    }

    @Override
    public boolean matchingReturn(MockRequest request, MockResponse response) {
        return true;
    }
}
