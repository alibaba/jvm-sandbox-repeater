package com.alibaba.repeater.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import com.alibaba.repeater.console.common.domain.MockInvocationBO;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * {@link MockInvocationConvert}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("MockInvocationConvert")
public class MockInvocationConvert implements ModelConverter<MockInvocation, MockInvocationBO> {

    @Override
    public MockInvocationBO convert(MockInvocation source) {
        MockInvocationBO bo = new MockInvocationBO();
        BeanUtils.copyProperties(source, bo);
        try {
            bo.setCurrentArgs(JacksonUtil.serialize(source.getCurrentArgs()));
        } catch (SerializeException e) {
            bo.setCurrentArgs(Arrays.toString(source.getCurrentArgs()));
        }
        try {
            bo.setOriginArgs(JacksonUtil.serialize(source.getOriginArgs()));
        } catch (SerializeException e) {
            bo.setCurrentArgs(Arrays.toString(source.getOriginArgs()));
        }
        return bo;
    }

    @Override
    public MockInvocation reconvert(MockInvocationBO target) {
        return null;
    }
}
