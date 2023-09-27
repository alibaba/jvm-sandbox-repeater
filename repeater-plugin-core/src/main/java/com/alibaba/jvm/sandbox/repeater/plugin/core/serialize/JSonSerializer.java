package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.google.common.io.BaseEncoding;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@link JSonSerializer} hessian序列化实现
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Serializer.class)
public class JSonSerializer extends AbstractSerializerAdapter {

    protected final static Logger log = LoggerFactory.getLogger(JSonSerializer.class);

    public JSONWriter.Feature[] features = new JSONWriter.Feature[]{
            JSONWriter.Feature.WriteClassName,
            JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.ReferenceDetection,
            JSONWriter.Feature.NotWriteDefaultValue,
            JSONWriter.Feature.WriteNameAsSymbol,
            JSONWriter.Feature.WriteEnumsUsingName
    };

    @Override
    public Type type() {
        return Type.JSON;
    }

    @Override
    public byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException {
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
            return JSON.toJSONBytes(object, features);
        } catch (Throwable t) {
            // may produce sof exception
            throw new SerializeException("[Error-1001]-json-serialize-error", t);
        } finally {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(swap);
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type, ClassLoader classLoader) throws SerializeException {
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
            try {
                return JSON.parseObject(bytes, type, ApplicationModel.getAutoTypeFilter(),
                        JSONReader.Feature.UseDefaultConstructorAsPossible,
                        JSONReader.Feature.UseNativeObject,
                        JSONReader.Feature.FieldBased,
                        JSONReader.Feature.SupportAutoType
                );
            } catch (Exception e) {
                return JSON.parseObject(bytes, type);
            }

        } catch (Throwable t) {
            log.error("SerializeException" ,t);
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        } finally {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(swap);
            }
        }
    }

    public <T> T deserializeIgnoreAutoType(String sequence, Class<T> type) throws SerializeException {
        if (sequence == null) {
            return null;
        }

        byte[] bytes = BaseEncoding.base64().decode(sequence);

        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            return JSON.parseObject(bytes, type, ApplicationModel.getAutoTypeFilter());
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        try {
            return JSON.parse(bytes);
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        }
    }
}
