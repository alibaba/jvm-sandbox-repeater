package com.alibaba.jvm.sandbox.repeater.plugin.hbase;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

/**
 * @description:
 * @author: ldb
 * @date: 2021-12-08
 **/
public class HbaseProcessor extends DefaultInvocationProcessor {

    public HbaseProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {

        if ("org.apache.hadoop.hbase.ipc.AbstractRpcClient".equals(event.javaClassName)) {
            Object argObject = event.argumentArray[2];
            try {
                Field scanField = FieldUtils.getDeclaredField(argObject.getClass(), "scan_", true);
                if (scanField != null) {
                    Object scanObject = scanField.get(argObject);
                    if (scanObject != null) {
                        Field filterField = FieldUtils.getDeclaredField(scanObject.getClass(), "filter_", true);
                        if (filterField != null && filterField.get(scanObject) != null) {
                            return new Object[]{filterField.get(scanObject)};
                        }
                    }
                }
                Field getField = FieldUtils.getDeclaredField(argObject.getClass(), "get_", true);
                if (getField != null) {
                    Object getObject = getField.get(argObject);
                    if (getObject != null) {
                        Field filterField = FieldUtils.getDeclaredField(getObject.getClass(), "filter_", true);
                        if (filterField != null && filterField.get(getObject) != null) {
                            return new Object[]{filterField.get(getObject)};
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                LogUtil.error("error occurred when assemble hbase request", e);
            }
        }

        if ("org.apache.hadoop.hbase.shaded.protobuf.ResponseConverter".equals(event.javaClassName)) {
            return new Object[]{event.argumentArray[1]};
        }

        return event.argumentArray;
    }


    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        return new Identity(getType().name(), event.javaClassName, event.javaMethodName, null);
    }


}
