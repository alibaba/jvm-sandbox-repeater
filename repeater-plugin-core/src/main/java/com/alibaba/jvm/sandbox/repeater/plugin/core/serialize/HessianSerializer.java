package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.caucho.hessian.io.*;
import com.google.common.collect.Maps;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * {@link HessianSerializer} hessian序列化实现
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Serializer.class)
public class HessianSerializer extends AbstractSerializerAdapter {

    private Map<String, SerializerFactory> cached = Maps.newConcurrentMap();

    private static boolean isJava8() {
        String javaVersion = System.getProperty("java.specification.version");
        return Double.valueOf(javaVersion) >= 1.8D;
    }

    @Override
    public Type type() {
        return Type.HESSIAN;
    }

    @Override
    public byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(getFactory(classLoader));
        try {
            output.writeObject(object);
            output.close();
        } catch (Throwable t) {
            // may produce sof exception
            throw new SerializeException("[Error-1001]-hessian-serialize-error", t);
        }
        return byteArray.toByteArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> type, ClassLoader classLoader) throws SerializeException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        input.setSerializerFactory(getFactory(classLoader));
        Object readObject;
        try {
            readObject = input.readObject(type);
            input.close();
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-hessian-deserialize-error", t);
        }
        return (T) readObject;
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        input.setSerializerFactory(getFactory(null));
        Object readObject;
        try {
            readObject = input.readObject();
            input.close();
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-hessian-deserialize-error", t);
        }
        return readObject;
    }

    /**
     * 通过classLoader获取序列化工厂；需要根据{@link ClassloaderBridge}来编解码类加载器
     *
     * @param classLoader 类加载器
     * @return 序列化工厂
     * @see com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge
     */
    private SerializerFactory getFactory(ClassLoader classLoader) {
        String token = getToken(classLoader);
        if (classLoader == null) {
            final SerializerFactory factory = new SerializerFactory();
            factory.setAllowNonSerializable(true);
            registerCustomFactory(factory);
            return factory;
        }
        SerializerFactory factory = cached.get(token);
        if (factory == null) {
            factory = new SerializerFactory(classLoader);
            factory.setAllowNonSerializable(true);
            registerCustomFactory(factory);
            cached.put(token, factory);
        }
        return factory;
    }

    private String getToken(ClassLoader classLoader) {
        ClassloaderBridge instance = ClassloaderBridge.instance();
        if (instance == null) {
            return classLoader == null ? "BootstrapClassLoader" : classLoader.getClass().getName();
        }
        return instance.encode(classLoader);
    }

    private void registerCustomFactory(SerializerFactory factory) {
        // try to register jdk8time
        if (isJava8()) {
            factory.addFactory(new Java8TimeSerializerFactory());
        }
        // add big decimal factory
        factory.addFactory(new BigDecimalSerializerFactory());
    }
}
