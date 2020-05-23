package com.alibaba.repeater.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.repeater.console.common.domain.InvocationBO;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * {@link InvocationConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("invocationConverter")
@Slf4j
public class InvocationConverter implements ModelConverter<Invocation, InvocationBO> {

    @Override
    public InvocationBO convert(Invocation source) {
        InvocationBO ibo = new InvocationBO();
        BeanUtils.copyProperties(source, ibo);
        Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        ibo.setIdentity(source.getIdentity().getUri());
        ibo.setInvokeType(source.getType().name());
        try {
            ibo.setRequest(hessian.deserialize(source.getRequestSerialized(), Object[].class));
            ibo.setResponse(hessian.deserialize(source.getResponseSerialized(), Object.class));
        } catch (SerializeException e) {
            log.error("error deserialize record wrapper", e);
        }
        ibo.setCost(source.getEnd() - source.getStart());
        return ibo;
    }

    @Override
    public Invocation reconvert(InvocationBO target) {
        return null;
    }
}
