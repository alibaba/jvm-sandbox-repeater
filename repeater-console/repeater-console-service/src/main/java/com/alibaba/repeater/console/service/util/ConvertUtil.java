package com.alibaba.repeater.console.service.util;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;

import java.util.Date;
import java.util.HashMap;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class ConvertUtil {

    public static Record convertWrapper(RecordWrapper wrapper, String body){
        Record record = new Record();
        record.setAppName(wrapper.getAppName());
        record.setEnvironment(wrapper.getEnvironment());
        record.setGmtCreate(new Date());
        record.setGmtRecord(new Date(wrapper.getTimestamp()));
        record.setHost(wrapper.getHost());
        record.setTraceId(wrapper.getTraceId());
        Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        try {
            Object response = hessian.deserialize(wrapper.getEntranceInvocation().getResponseSerialized(), Object.class);
            if (response instanceof String) {
                record.setResponse(convert2Json((String)response));
            } else {
                record.setResponse(JacksonUtil.serialize(response));
            }
            record.setRequest(JacksonUtil.serialize(hessian.deserialize(wrapper.getEntranceInvocation().getRequestSerialized(), Object[].class)));
        } catch (SerializeException e) {
            // ignore
        }
        record.setEntranceDesc(wrapper.getEntranceDesc());
        record.setWrapperRecord(body);
        return record;
    }

    public static String convert2Json(String json) {
        try {
            return JacksonUtil.serialize(JacksonUtil.deserialize(json, HashMap.class));
        } catch (SerializeException e) {
            return json;
        }
    }
}
