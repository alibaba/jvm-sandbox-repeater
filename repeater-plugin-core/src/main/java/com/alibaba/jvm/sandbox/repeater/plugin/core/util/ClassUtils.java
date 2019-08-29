package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@Deprecated
public class ClassUtils {

    private static final Map<String, Class> JVM_BASIC_TYPE_MAP = Maps.newHashMap();

    static {
        JVM_BASIC_TYPE_MAP.put("long", long.class);
        JVM_BASIC_TYPE_MAP.put("short", short.class);
        JVM_BASIC_TYPE_MAP.put("double", double.class);
        JVM_BASIC_TYPE_MAP.put("char", char.class);
        JVM_BASIC_TYPE_MAP.put("float", float.class);
        JVM_BASIC_TYPE_MAP.put("byte", byte.class);
        JVM_BASIC_TYPE_MAP.put("int", int.class);
        JVM_BASIC_TYPE_MAP.put("boolean", boolean.class);
    }

    /**
     * 找到对应的类
     *
     * @param classNames  签名类
     * @param classLoader 类加载器
     * @return 签名类
     * @throws ClassNotFoundException 无法加载类异常
     */
    public static Class<?>[] findClasses(String[] classNames, ClassLoader classLoader) throws ClassNotFoundException {
        Class[] classes = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            Class<?> basic = JVM_BASIC_TYPE_MAP.get(className);
            if (basic != null) {
                classes[i] = basic;
            } else {
                classes[i] = Class.forName(className, true, classLoader);
            }
        }
        return classes;
    }

    /**
     * 根据参数转换成 type
     *
     * @param arguments 入参
     * @return 入参类型
     */
    public static Class<?>[] toClass(Object[] arguments) {
        if (arguments == null) {
            return null;
        } else if (arguments.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        final Class<?>[] classes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            classes[i] = arguments[i] == null ? null : arguments[i].getClass();
        }
        return classes;
    }
}
