package com.alibaba.jvm.sandbox.repeater.plugin.java;

import com.google.common.collect.Maps;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * {@link JavaInstanceCache}
 * <p>
 * Java实例缓存，作用是将拦截到的JavaEntrance缓存起来，作为{@link JavaRepeater}获取java运行实例的补充
 * <p>
 * 该方法的局限性在于，必须该实例的埋点被采样到
 * </p>
 *
 * @author zhaoyb1990
 */
class JavaInstanceCache {

    /**
     * key   : className
     * value : instance
     */
    private static Map<String, Object> CACHED = Maps.newConcurrentMap();


    /**
     * 根据实例的类名缓存
     * <p>
     * 注意问题:
     * 1. 多实例问题可能导致回放失败
     * </p>
     *
     * @param instance 实例
     */
    static void cacheInstance(Object instance) {
        if (instance != null) {
            Class<?> clazz;
            if (Proxy.isProxyClass(instance.getClass())) {
                clazz = Proxy.getInvocationHandler(instance).getClass();
            } else {
                clazz = instance.getClass();
            }
            CACHED.put(clazz.getCanonicalName(), instance);
        }
    }

    /**
     * 通过类找到实例
     * <p>
     * 注意问题：
     * 1. 实例的缓存时机是被回放的埋点被采样到（否则sandbox无法感知到实例）
     * </p>
     *
     * @param className 类全名
     * @return 实例
     */
    static Object getInstance(String className) {
        return CACHED.get(className);
    }
}
