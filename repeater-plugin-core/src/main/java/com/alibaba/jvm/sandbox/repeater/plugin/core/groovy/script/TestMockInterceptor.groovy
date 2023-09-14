package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.script

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.StrategyProvider
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy

class TestMockInterceptor implements MockInterceptor {

    @Override
    void beforeSelect(MockRequest request) {
    }

    @Override
    void beforeReturn(MockRequest request, MockResponse response) {
        String uri = request.getIdentity().getUri();
        if (response.getInvocation()==null && response.isInvocationNotFund()) {
            LogUtil.info("test mock id={}",uri);
            if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.selectByExample")) {
                request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.selectByExample");
                MockResponse response2 =  StrategyProvider.instance().provide(MockStrategy.StrategyType.PARAMETER_MATCH).execute(request);
                replace(response, response2);
                request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.selectByExample");
                return;
            }

            if (uri.equals("mybatis://INSERT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.insertSelective")) {
                request.getIdentity().setUri("mybatis://INSERT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.insertSelective");
                MockResponse response2 =  StrategyProvider.instance().provide(MockStrategy.StrategyType.PARAMETER_MATCH).execute(request);
                replace(response, response2);
                request.getIdentity().setUri("mybatis://INSERT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.insertSelective");
                return;
            }
        }

    }

    MockResponse replace(MockResponse origin, MockResponse current) {
        if (current.isInvocationNotFund()) {
            return;
        }

        origin.setInvocationNotFund(false);
        origin.setInvocation(current.getInvocation());
        origin.setAction(current.getAction())
        origin.setThrowable(current.getThrowable())
    }


    @Override
    boolean matchingSelect(MockRequest request) {
        return false
    }

    @Override
    boolean matchingReturn(MockRequest request, MockResponse response) {
        if (request.getType().equals(InvokeType.MYBATIS)) {
            return true;
        }

        return false
    }
}
