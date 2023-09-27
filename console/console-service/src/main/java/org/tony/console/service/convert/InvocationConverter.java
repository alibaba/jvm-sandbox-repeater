package org.tony.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.*;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.InvocationBO;

import java.util.HashMap;
import java.util.List;

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
        Serializer serializer = SerializerWrapper.getSerializer(source.getSerializeType());

        ibo.setIdentity(source.getIdentity().getUri());
        ibo.setInvokeType(source.getType().name());
        try {
            if (serializer.type().equals(Serializer.Type.JSON)) {
                JSonSerializer jSonSerializer = (JSonSerializer) serializer;
                ibo.setRequest(jSonSerializer.deserializeIgnoreAutoType(source.getRequestSerialized(), Object[].class));
                ibo.setResponse(jSonSerializer.deserializeIgnoreAutoType(source.getResponseSerialized(), Object.class));
            } else if (serializer.type().equals(Serializer.Type.JSONB)) {
                JSONBSerializer jSonSerializer = (JSONBSerializer) serializer;
                ibo.setRequest(jSonSerializer.deserializeIgnoreAutoType(source.getRequestSerialized(), Object[].class));
                ibo.setResponse(jSonSerializer.deserializeIgnoreAutoType(source.getResponseSerialized(), Object.class));
            } else {
                ibo.setRequest(serializer.deserialize(source.getRequestSerialized(), Object[].class));
                ibo.setResponse(serializer.deserialize(source.getResponseSerialized(), Object.class));
            }

        } catch (SerializeException e) {
            log.error("error deserialize record wrapper", e);
        }
        ibo.setCost(source.getEnd() - source.getStart());
        return ibo;
    }

    @Override
    public List<InvocationBO> convert(List<Invocation> invocations) {
        return null;
    }

    @Override
    public List<Invocation> reconvertList(List<InvocationBO> sList) {
        return null;
    }

    @Override
    public Invocation reconvert(InvocationBO target) {
        return null;
    }

}
