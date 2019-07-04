package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.service.RecordService;
import com.alibaba.repeater.console.service.util.ConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link RecordServiceLocalImpl} 本地内存存储(示例DEMO）
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("recordServiceLocal")
public class RecordServiceLocalImpl extends AbstractRecordService implements RecordService {

    /**
     * key:appName_traceId
     */
    private volatile Map<String, Record> recordCache = new ConcurrentHashMap<String, Record>(4096);

    /**
     * key:repeatId
     */
    private volatile Map<String, Record> repeatCache = new ConcurrentHashMap<String, Record>(4096);

    /**
     * key:repeatId
     */
    private volatile Map<String, RepeatModel> repeatModelCache = new ConcurrentHashMap<String, RepeatModel>(4096);

    @Override
    public RepeaterResult<String> saveRecord(String body) {
        try {
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(body, RecordWrapper.class);
            if (wrapper == null || StringUtils.isEmpty(wrapper.getAppName())) {
                return RepeaterResult.builder().success(false).message("invalid request").build();
            }
            Record record = ConvertUtil.convertWrapper(wrapper, body);
            recordCache.put(buildUniqueKey(wrapper.getAppName(), wrapper.getTraceId()), record);
            return RepeaterResult.builder().success(true).message("operate success").data("-/-").build();
        } catch (Throwable throwable) {
            return RepeaterResult.builder().success(false).message(throwable.getMessage()).build();
        }
    }

    @Override
    public RepeaterResult<String> saveRepeat(String body) {
        try {
            RepeatModel rm = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
            Record record = repeatCache.remove(rm.getRepeatId());
            if (record == null) {
                return RepeaterResult.builder().success(false).message("invalid repeatId:" + rm.getRepeatId()).build();
            }
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(record.getWrapperRecord(), RecordWrapper.class);
            rm.setOriginResponse(SerializerWrapper.hessianDeserialize(wrapper.getEntranceInvocation().getResponseSerialized()));
            repeatModelCache.put(rm.getRepeatId(), rm);
        } catch (Throwable throwable) {
            return RepeaterResult.builder().success(false).message(throwable.getMessage()).build();
        }
        return RepeaterResult.builder().success(true).message("operate success").data("-/-").build();
    }

    @Override
    public RepeaterResult<String> get(String appName, String traceId) {
        Record record = recordCache.get(buildUniqueKey(appName, traceId));
        if (record == null) {
            return RepeaterResult.builder().success(false).message("data not exits").build();
        }
        return RepeaterResult.builder().success(true).message("operate success").data(record.getWrapperRecord()).build();
    }

    @Override
    public RepeaterResult<String> repeat(String appName, String traceId, String repeatId) {
        final Record record = recordCache.get(buildUniqueKey(appName, traceId));
        if (record == null) {
            return RepeaterResult.builder().success(false).message("data does not exist").build();
        }
        RepeaterResult<String> pr = repeat(record, repeatId);
        if (pr.isSuccess()) {
            repeatCache.put(pr.getData(), record);
        }
        return pr;
    }

    @Override
    public RepeaterResult<RepeatModel> callback(String repeatId) {
        if (repeatCache.containsKey(repeatId)) {
            return RepeaterResult.builder().success(true).message("operate is going on").build();
        }
        RepeatModel rm = repeatModelCache.get(repeatId);
        // 进行Diff
        if (rm == null) {
            return RepeaterResult.builder().success(false).message("invalid repeatId:" + repeatId).build();
        }
        return RepeaterResult.builder().success(true).message("operate success").data(rm).build();
    }

    private String buildUniqueKey(String appName, String traceId) {
        return appName + "-" + traceId;
    }
}
