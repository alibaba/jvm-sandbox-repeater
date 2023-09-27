package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.script

import com.alibaba.jvm.sandbox.repeater.plugin.core.groovy.util.InvocationSelectUtil
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvocationHandler

class TestInvocationHandler implements InvocationHandler{
    @Override
    MockResponse executeNotFundInvocation(MockRequest request) {

        String uri = request.getIdentity().getUri();

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.selectByExample")) {
            request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.selectByExample");
        }

        if (uri.equals("mybatis://INSERT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.insertSelective")) {
            request.getIdentity().setUri("mybatis://INSERT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.insertSelective");
        }

        if (uri.equals("mybatis://UPDATE/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.updateBookkeepingStatusByPackageNo")) {
            request.getIdentity().setUri("mybatis://UPDATE/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.updateBookkeepingStatusByPackageNo");
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryPackageByPackageNos")) {
            request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageNewMapper.queryPackageByPackageNos");
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryByPackageNo")) {
            request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.queryByPackageNo");
        }

        if (uri.equals("mybatis://UPDATE/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.updateByExampleSelective")) {
            request.getIdentity().setUri("mybatis://UPDATE/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.updateByExampleSelective");
        }

        if (uri.equals("mybatis://DELETE/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.deleteByExample")) {
            request.getIdentity().setUri("mybatis://DELETE/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.deleteByExample");
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryPackages4Refund")) {
            request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.queryPackages4Refund");
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.countPackages4Refund")) {
            request.getIdentity().setUri("mybatis://SELECT/com.nio.dd.mer.order.dao.custom.mapper.PackageCustomMapper.countPackages4Refund");
        }

        SelectResult select = InvocationSelectUtil.select(request);
        Invocation invocation = select.getInvocation();
        if (select.isMatch() && invocation != null) {
            MockResponse response = MockResponse.builder()
                    .action(invocation.getThrowable() == null ? MockResponse.Action.RETURN_IMMEDIATELY : MockResponse.Action.THROWS_IMMEDIATELY)
                    .throwable(invocation.getThrowable())
                    .invocation(invocation)
                    .build();

            return response;
        }

        return null
    }

    @Override
    boolean matchingNotFund(MockRequest request) {
        if (request.getType() != InvokeType.MYBATIS) {
            return false;
        }

        String uri = request.getIdentity().getUri();

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.selectByExample")) {
            return true;
        }

        if (uri.equals("mybatis://INSERT/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.insertSelective")) {
            return true;
        }

        if (uri.equals("mybatis://UPDATE/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.updateBookkeepingStatusByPackageNo")) {
            return true;
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryPackageByPackageNos")) {
            return true;
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryByPackageNo")) {
            return true;
        }

        if (uri.equals("mybatis://UPDATE/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.updateByExampleSelective")) {
            return true;
        }

        if (uri.equals("mybatis://DELETE/com.nio.dd.mer.order.dao.auto.mapper.PackageMapper.deleteByExample")) {
            return true;
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.queryPackages4Refund")) {
            return true;
        }

        if (uri.equals("mybatis://SELECT/com.nio.dd.mer.order.dao.auto.ext.ExtPackageMapper.countPackages4Refund")) {
            return true;
        }

        return false
    }
}
