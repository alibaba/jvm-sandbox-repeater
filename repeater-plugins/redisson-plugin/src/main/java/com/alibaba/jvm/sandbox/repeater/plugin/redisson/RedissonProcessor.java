package com.alibaba.jvm.sandbox.repeater.plugin.redisson;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

/**
 * {@link RedissonProcessor} redis客户端redisson子插件
 *
 * @author quansong
 * @version 1.0
 */
class RedissonProcessor extends DefaultInvocationProcessor {

    RedissonProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object mapperMethod = event.target;
        Object name = null;
        Field field = FieldUtils.getField(mapperMethod.getClass(), "name", true);
        if(field == null){
            return new Object[]{event.argumentArray};
        }
        try {
            name = field.get(mapperMethod);
            return new Object[]{name, event.argumentArray};
        } catch (IllegalAccessException e) {
            return new Object[]{event.argumentArray};
        }
    }

}
