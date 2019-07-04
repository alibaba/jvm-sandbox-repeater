package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

/**
 * {@link Serializer} 序列化工具
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Serializer {

    /**
     * 默认字符集
     */
    String defaultCharset = "ISO-8859-1";

    /**
     * 获取序列化器的类型
     *
     * @return type 类型
     * @see com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer.Type
     */
    Type type();

    /**
     * 序列化java对象到byte数组
     *
     * @param object 序列化的对象
     * @return 序列化的byte
     * @throws SerializeException 序列化异常
     */
    byte[] serialize(Object object) throws SerializeException;

    /**
     * 序列化java对象到byte数组
     *
     * @param object      序列化的对象
     * @param classLoader 类加载器
     * @return 序列化的byte
     * @throws SerializeException 序列化异常
     */
    byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException;

    /**
     * 序列化java对象到字符串
     *
     * @param object 序列化的对象
     * @return 序列化后的字符串 默认字符集采用 {@link Serializer#defaultCharset}
     * @throws SerializeException 序列化异常
     */
    String serialize2String(Object object) throws SerializeException;

    /**
     * 序列化java对象到字符串
     *
     * @param object      序列化的对象
     * @param classLoader 类加载器
     * @return 序列化后的字符串 默认字符集采用 {@link Serializer#defaultCharset}
     * @throws SerializeException 序列化异常
     */
    String serialize2String(Object object, ClassLoader classLoader) throws SerializeException;

    /**
     * 将bytes二进制数组反序列化到目标对象
     *
     * @param bytes 序列化的二进制数组
     * @param type  泛型类型
     * @param <T>   泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws SerializeException;

    /**
     * 将bytes二进制数组反序列化到目标对象
     *
     * @param bytes       序列化的二进制数组
     * @param classLoader 类加载器
     * @param type        泛型类型
     * @param <T>         泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    <T> T deserialize(byte[] bytes, Class<T> type, ClassLoader classLoader) throws SerializeException;

    /**
     * 将sequence字符串反序列化到目标对象
     *
     * @param sequence 序列化后的字符串
     * @param type     泛型类型
     * @param <T>      泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    <T> T deserialize(String sequence, Class<T> type) throws SerializeException;

    /**
     * 无泛型反序列化
     *
     * @param sequence 序列化后的字符串
     * @return 反序列化对象
     * @throws SerializeException 序列化异常
     */
    Object deserialize(String sequence) throws SerializeException;

    /**
     * 无泛型反序列化
     *
     * @param bytes 序列化的二进制数组
     * @return 反序列化对象
     * @throws SerializeException 序列化异常
     */
    Object deserialize(byte[] bytes) throws SerializeException;

    /**
     * 将sequence字符串反序列化到目标对象
     *
     * @param sequence    序列化后的字符串
     * @param classLoader 类加载器
     * @param type        泛型类型
     * @param <T>         泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException 序列化异常
     */
    <T> T deserialize(String sequence, Class<T> type, ClassLoader classLoader) throws SerializeException;

    enum Type {
        HESSIAN,
        JSON,
        JAVA,
        NONE
    }
}
