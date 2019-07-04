package com.alibaba.repeater.console.service.util;

import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;

import java.util.Date;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class ConvertUtil {

    public static Record convertWrapper(RecordWrapper wrapper,String body){
        Record record = new Record();
        record.setAppName(wrapper.getAppName());
        record.setEnvironment(wrapper.getEnvironment());
        record.setGmtCreate(new Date());
        record.setGmtRecord(new Date(wrapper.getTimestamp()));
        record.setHost(wrapper.getHost());
        record.setTraceId(wrapper.getTraceId());
        record.setWrapperRecord(body);
        return record;
    }
}
