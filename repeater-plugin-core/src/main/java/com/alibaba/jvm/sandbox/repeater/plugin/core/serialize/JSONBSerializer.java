package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.google.common.io.BaseEncoding;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng.hu1
 * @Date 2023/9/1 16:32
 */
@MetaInfServices(Serializer.class)
public class JSONBSerializer extends AbstractSerializerAdapter {

    protected final static Logger log = LoggerFactory.getLogger(JSONBSerializer.class);

    public JSONWriter.Feature[] features = new JSONWriter.Feature[]{
            JSONWriter.Feature.WriteClassName,
            JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.ReferenceDetection,
            JSONWriter.Feature.NotWriteDefaultValue,
            JSONWriter.Feature.WriteNameAsSymbol,
            JSONWriter.Feature.WriteEnumsUsingName,
            JSONWriter.Feature.BeanToArray
    };

    @Override
    public Type type() {
        return Type.JSONB;
    }

    @Override
    public byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException {
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }
            return JSONB.toBytes(object, features);
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
                return JSONB.parseObject(bytes, type, ApplicationModel.getAutoTypeFilter(),
                        JSONReader.Feature.UseDefaultConstructorAsPossible,
                        JSONReader.Feature.UseNativeObject,
                        JSONReader.Feature.FieldBased,
                        JSONReader.Feature.SupportArrayToBean,
                        JSONReader.Feature.SupportAutoType
                );
            } catch (Exception e) {
                return JSONB.parseObject(bytes, type);
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
            return JSONB.parseObject(bytes, type, ApplicationModel.getAutoTypeFilter());
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        }
    }


    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        try {
            return JSONB.parse(bytes);
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-json-deserialize-error", t);
        }
    }
}
