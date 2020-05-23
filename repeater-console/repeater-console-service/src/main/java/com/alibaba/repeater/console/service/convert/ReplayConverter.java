package com.alibaba.repeater.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import com.alibaba.repeater.console.common.domain.DifferenceBO;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.domain.ReplayStatus;
import com.alibaba.repeater.console.dal.model.Replay;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link ReplayConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("replayConverter")
public class ReplayConverter implements ModelConverter<Replay, ReplayBO> {

    @Resource
    private RecordDetailConverter recordDetailConverter;
    @Resource
    private MockInvocationConvert mockInvocationConvert;

    @Override
    public ReplayBO convert(Replay source) {
        ReplayBO rbo = new ReplayBO();
        BeanUtils.copyProperties(source, rbo);
        try {
            List<MockInvocation> mockInvocations = JacksonUtil.deserializeArray(source.getMockInvocation(), MockInvocation.class);
            rbo.setMockInvocations(
                    Optional.ofNullable(mockInvocations)
                            .orElse(Collections.emptyList())
                            .stream().map(mockInvocationConvert::convert)
                            .collect(Collectors.toList())
            );
            rbo.setDifferences(JacksonUtil.deserializeArray(source.getDiffResult(), DifferenceBO.class));
        } catch (SerializeException e) {
            //
        }
        rbo.setStatus(ReplayStatus.of(source.getStatus()));
        rbo.setRecord(recordDetailConverter.convert(source.getRecord()));
        return rbo;
    }

    @Override
    public Replay reconvert(ReplayBO target) {
        return null;
    }
}
