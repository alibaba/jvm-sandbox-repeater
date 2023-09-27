package org.tony.console.service.utils;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.HttpInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.tony.console.common.domain.RecordType;
import org.tony.console.db.model.Record;

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

        Invocation invocation = wrapper.getEntranceInvocation();
        Serializer serializer = SerializerWrapper.getSerializer(invocation.getSerializeType());
        try {
            Object response = serializer.deserialize(wrapper.getEntranceInvocation().getResponseSerialized(), Object.class);
            if (response instanceof String) {
                record.setResponse(convert2Json((String)response));
            } else {
                record.setResponse(JacksonUtil.serialize(response));
            }
            record.setRequest(JacksonUtil.serialize(serializer.deserialize(wrapper.getEntranceInvocation().getRequestSerialized(), Object[].class)));
        } catch (SerializeException e) {
            // ignore
        }
        if (invocation instanceof HttpInvocation) {
            record.setEntranceDesc(((HttpInvocation)invocation).getRequestURI());
            //这里按需调整对应的版本信息
            record.setVersion(1);
        } else {
            record.setEntranceDesc(wrapper.getEntranceDesc());
            record.setVersion(0);
        }

        record.setWrapperRecord(body);

        InvokeType invokeType = invocation.getType();

        if (invokeType.equals(InvokeType.JAVA)) {
            record.setType(RecordType.JAVA.type);
        } else {
            record.setType(RecordType.HTTP.type);
        }

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
