package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.kohsuke.MetaInfServices;

/**
 * {@link JSonSerializer} hessian序列化实现
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Serializer.class)
public class JSonSerializer extends AbstractSerializerAdapter {

    private SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.IgnoreErrorGetter,
            SerializerFeature.IgnoreNonFieldGetter,
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.SkipTransientField,
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
            return JSON.parseObject(bytes, type);
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        } finally {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(swap);
            }
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