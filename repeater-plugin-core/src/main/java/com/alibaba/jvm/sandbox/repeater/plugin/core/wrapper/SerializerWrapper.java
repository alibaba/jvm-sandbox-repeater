package com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer.Type;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * {@link SerializerWrapper} 序列化工具；目标能够直接序列化{@link RecordModel}
 * <p>
 *
 * @author zhaoyb1990
 */
public class SerializerWrapper {

    private static SerializerProvider provider = SerializerProvider.instance();

    protected static Logger log = LoggerFactory.getLogger(SerializerWrapper.class);

    /**
     * 传输对象默认采用JSON序列化
     *
     * @param object 包装对象
     * @return 序列化字符串
     * @throws SerializeException 序列化异常
     */
    public static String jsonSerialize(Object object) throws SerializeException {
        return provider.provide(Type.JSON).serialize2String(object);
    }

    /**
     * 反序列化recordWrapper；传输对象默认采用JSON序列化
     *
     * @param sequence 序列化字符串
     * @param tClass   对象类型
     * @param <T>      泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    public static <T> T jsonDeserialize(String sequence, Class<T> tClass) throws SerializeException {
        return provider.provide(Type.JSON).deserialize(sequence, tClass);
    }

    /**
     * hessian序列化
     *
     * @param object 对象
     * @return 序列化字符串
     * @throws SerializeException 序列化异常
     */
    public static String hessianSerialize(Object object) throws SerializeException {
        return provider.provide(Type.HESSIAN).serialize2String(object);
    }

    public static byte[] hessianSerialize2Byte(Object object) throws SerializeException {
        return provider.provide(Type.HESSIAN).serialize(object);
    }

    public static <T> T hessianDeserializeByte(byte[] bytes, Class<T> t) throws SerializeException {
        return  provider.provide(Type.HESSIAN).deserialize(bytes, t);
    }

    /**
     * hessian序列化
     *
     * @param object 对象
     * @return 序列化字符串
     * @throws SerializeException 序列化异常
     */
    public static String hessianSerialize(Object object, ClassLoader classLoader) throws SerializeException {
        return provider.provide(Type.HESSIAN).serialize2String(object, classLoader);
    }

    /**
     * hessian反序列化
     *
     * @param sequence 序列化字符串
     * @param tClass   对象类型
     * @param <T>      泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    public static <T> T hessianDeserialize(String sequence, Class<T> tClass) throws SerializeException {
        return provider.provide(Type.HESSIAN).deserialize(sequence, tClass);
    }

    /**
     * hessian反序列化
     *
     * @param sequence 序列化字符串
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    public static Object hessianDeserialize(String sequence) throws SerializeException {
        return provider.provide(Type.HESSIAN).deserialize(sequence);
    }


    public static Serializer getSerializer(String type) {
        String serializeType = "HESSIAN";
        if (type == null && ApplicationModel.instance().getConfig()!=null) {
            serializeType = ApplicationModel.instance().getConfig().getSerializeType();
        } else {
            serializeType = type;
        }

        if (Type.JSON.name().equalsIgnoreCase(serializeType)) {
            return provider.provide(Type.JSON);
        }

        if (Type.JSONB.name().equalsIgnoreCase(serializeType)) {
            return provider.provide(Type.JSONB);
        }

        return provider.provide(Type.HESSIAN);
    }

    /**
     * 及时序列化
     *
     * @param invocation 调用信息
     */
    public static void inTimeSerialize(Invocation invocation) throws SerializeException {
        Serializer serializer = getSerializer(null);
        invocation.setSerializeType(serializer.type().name());

        if (invocation.getResponse() != null && invocation.getResponseSerialized() == null) {
            invocation.setResponseSerialized(serializer
                    .serialize2String(invocation.getResponse(), invocation.getClassLoader()));
        }
        if (invocation.getRequest() != null && invocation.getRequestSerialized() == null) {
            invocation.setRequestSerialized(serializer
                    .serialize2String(invocation.getRequest(), invocation.getClassLoader()));
        }
        if (invocation.getThrowable() != null && invocation.getThrowableSerialized() == null) {
            invocation.setThrowableSerialized(serializer
                    .serialize2String(invocation.getThrowable(), invocation.getClassLoader()));
        }
    }

    /**
     * 及时序列化(回放时只需要解出request)
     *
     * @param invocation 调用信息
     */
    public static void inTimeDeserialize(Invocation invocation) throws SerializeException {
        Serializer serializer = getSerializer(invocation.getSerializeType());
        inTimeDeserializeRequest(invocation);
        inTimeDeserializeResponse(invocation);

        if (invocation.getThrowable() == null && invocation.getThrowableSerialized() != null) {
            invocation.setThrowable((Throwable) serializer.deserialize(invocation.getThrowableSerialized(), null,
                    ClassloaderBridge.instance().decode(invocation.getSerializeToken())));
        }
    }

    private static void inTimeDeserializeResponse(Invocation invocation) throws SerializeException {
        Serializer serializer = getSerializer(invocation.getSerializeType());
        if (invocation.getResponse() == null && invocation.getResponseSerialized() != null) {


            if (StringUtils.isNotBlank(invocation.getResponseCls())) {

                try {
                    String clsName = invocation.getResponseCls();
                    //如果发现反序列化回来的对象类型不一致，强制进行类型纠正
                    ClassLoader classLoader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());

                    Class cls = Class.forName(clsName, true, classLoader);
                    Object obj = serializer.deserialize(invocation.getResponseSerialized(), cls, classLoader);
                    invocation.setResponse(obj);

                } catch (Exception e) {
                    log.error("system error", e);
                }

            } else {
                invocation.setResponse(serializer.deserialize(invocation.getResponseSerialized(), null,
                        ClassloaderBridge.instance().decode(invocation.getSerializeToken())));
            }


        }
    }

    private static void inTimeDeserializeRequest(Invocation invocation) throws SerializeException {
        Serializer serializer = getSerializer(invocation.getSerializeType());

        ClassLoader classLoader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());

        if (invocation.getRequest() == null && invocation.getRequestSerialized() != null) {
            invocation.setRequest((Object[]) serializer.deserialize(
                    invocation.getRequestSerialized(),
                    Object[].class,
                    classLoader
            ));

            boolean modify = false;

            //参数类型进行二次校验
            if (invocation.getSerializeType().equals("JSON")
                    && invocation.getRequestCls()!=null
                    && invocation.getRequestCls().length>0
                    && invocation.getRequest().length>0
            ) {
                //做参数检查并替换
                for (int i=0; i<invocation.getRequestCls().length; i++) {
                    Object param = invocation.getRequest()[i];
                    if (param == null) {
                        continue;
                    }

                    String clsName = invocation.getRequestCls()[i];
                    //说明这里是个数组
                    if(clsName.equals("[Ljava.lang.Object;") && param.getClass().getName().equals("java.util.ArrayList")) {
                        ArrayList list = (ArrayList)param;
                        invocation.getRequest()[i] = list.toArray();
                        continue;
                    }

                    try {
                        //如果发现反序列化回来的对象类型不一致，强制进行类型纠正
                        if (!clsName.equals(param.getClass().getName())) {
                            Class cls = Class.forName(clsName, true, classLoader);
                            Object paramNew = serializer.deserialize(serializer.serialize(param, classLoader), cls, classLoader);
                            invocation.getRequest()[i] = paramNew;
                            modify = true;
                        }
                    } catch (Exception e) {
                        log.error("system error", e);
                    }
                }

                //如果发生了变更，这里需要重新刷新下序列化后的字段，方便后续的参数匹配逻辑
                if (modify) {
                    invocation.setRequestSerialized(SerializerWrapper.hessianSerialize(invocation.getRequest(), classLoader));
                }
            }
        }
    }
}
