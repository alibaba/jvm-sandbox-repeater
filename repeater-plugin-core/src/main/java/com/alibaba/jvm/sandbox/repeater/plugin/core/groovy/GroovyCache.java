package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvocationHandler;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author peng.hu1
 * @Date 2023/3/28 18:50
 */
public class GroovyCache {

    private static Map<Long, MockInterceptor> groovyMockStrategyCache = new ConcurrentHashMap<>();

    private static Map<Long, InvocationHandler> groovyInvocationHandlerCache = new ConcurrentHashMap<>();

    private static Map<Long, Integer> groovyVersionMap = new ConcurrentHashMap<>();

    public static  Map<Long, MockInterceptor> getMockStrategyMap() {
        return groovyMockStrategyCache;
    }

    public static Map<Long, InvocationHandler> getGroovyInvocationHandlerCache() {
        return groovyInvocationHandlerCache;
    }

    public static boolean contains(GroovyConfig groovyConfig) {
        if (groovyVersionMap.containsKey(groovyConfig.getId()) &&
                groovyVersionMap.get(groovyConfig.getId()).equals(groovyConfig.getVersion())
        ) {
            return true;
        }

        return false;
    }

    public static void removeMockStrategy(Long id) {
        groovyMockStrategyCache.remove(id);
        groovyVersionMap.remove(id);
    }

    public static void addMockStrategy(Long id, MockInterceptor obj, int version) {
        groovyMockStrategyCache.put(id, obj);
        groovyVersionMap.put(id, version);
    }

    public static void removeInvocationHandler(Long id) {
        groovyInvocationHandlerCache.remove(id);
        groovyVersionMap.remove(id);
    }

    public static void addInvocationHandler(Long id, InvocationHandler obj, int version) {
        groovyInvocationHandlerCache.put(id, obj);
        groovyVersionMap.put(id, version);
    }
}
