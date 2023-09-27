package org.tony.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.domain.MockInvocationBO;
import org.tony.console.service.utils.JacksonUtil;

import java.util.Arrays;
import java.util.List;

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

        if (!CollectionUtils.isEmpty(bo.getDiffs())){
            bo.setCompareSuccess(false);
        } else {
            bo.setCompareSuccess(true);
        }
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
    public List<MockInvocationBO> convert(List<MockInvocation> mockInvocations) {
        return null;
    }

    @Override
    public List<MockInvocation> reconvertList(List<MockInvocationBO> sList) {
        return null;
    }

    @Override
    public MockInvocation reconvert(MockInvocationBO target) {
        return null;
    }
}
