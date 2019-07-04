package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.service.RecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractRecordService implements RecordService {

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    protected RepeaterResult<String> repeat(Record record, String repeatId) {
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(record.getAppName());
        meta.setTraceId(record.getTraceId());
        meta.setMock(true);
        meta.setRepeatId(StringUtils.isEmpty(repeatId) ? TraceGenerator.generate() : repeatId);
        meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
        Map<String, String> requestParams = new HashMap<String, String>(2);
        try {
            requestParams.put(Constants.DATA_TRANSPORT_IDENTIFY, SerializerWrapper.hessianSerialize(meta));
        } catch (SerializeException e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        }
        HttpUtil.Resp resp = HttpUtil.doPost(repeatURL, requestParams);
        if (resp.isSuccess()) {
            return RepeaterResult.builder().success(true).message("operate success").data(meta.getRepeatId()).build();
        }
        return RepeaterResult.builder().success(false).message("operate failed").data(resp).build();
    }
}
