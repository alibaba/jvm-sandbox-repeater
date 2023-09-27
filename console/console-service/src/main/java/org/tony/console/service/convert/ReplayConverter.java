package org.tony.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.DifferenceBO;
import org.tony.console.common.domain.ReplayBO;
import org.tony.console.common.domain.ReplayStatus;
import org.tony.console.db.model.Replay;
import org.tony.console.service.utils.JacksonUtil;

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
//        rbo.setRecord(recordDetailConverter.convert(source.getRecord()));
        return rbo;
    }

    @Override
    public List<ReplayBO> convert(List<Replay> replays) {
        return null;
    }

    @Override
    public List<Replay> reconvertList(List<ReplayBO> sList) {
        return null;
    }

    @Override
    public Replay reconvert(ReplayBO target) {
        return null;
    }
}
