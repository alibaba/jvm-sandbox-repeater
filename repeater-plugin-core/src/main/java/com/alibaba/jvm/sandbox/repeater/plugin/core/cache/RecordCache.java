package com.alibaba.jvm.sandbox.repeater.plugin.core.cache;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * {@link RecordCache} 录制缓存
 * <p>
 *
 * @author zhaoyb1990
 */
public class RecordCache {

    private static final LoadingCache<Integer, Invocation> INVOCATION_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(new CacheLoader<Integer, Invocation>() {
                @Override
                public Invocation load(Integer s) throws Exception {
                    return new Invocation();
                }
            });

    private static final LoadingCache<String, List<Invocation>> SUB_INVOCATION_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<Invocation>>() {
                @Override
                public List<Invocation> load(String s){
                    return Lists.newArrayList();
                }
            });

    /**
     * 缓存调用；根据{@link com.alibaba.jvm.sandbox.api.event.InvokeEvent#invokeId}进行缓存，根据traceId在多入口场景下会乱
     *
     * @param invokeId   调用ID
     * @param invocation 调用
     */
    public static void cacheInvocation(int invokeId, Invocation invocation) {
        INVOCATION_CACHE.put(invokeId, invocation);
    }

    /**
     * 获取调用
     *
     * @param invokeId 调用ID
     * @return 调用
     */
    public static Invocation getInvocation(int invokeId) {
        return INVOCATION_CACHE.getIfPresent(invokeId);
    }

    /**
     * 缓存子调用
     *
     * @param invocation 子调用
     */
    public static void cacheSubInvocation(Invocation invocation) {
        try {
            SUB_INVOCATION_CACHE.get(invocation.getTraceId()).add(invocation);
        } catch (ExecutionException e) {
            // impossible
        }
    }

    public static List<Invocation> getSubInvocation(String traceId) {
        try{
            return SUB_INVOCATION_CACHE.getIfPresent(traceId);
        } finally {
            SUB_INVOCATION_CACHE.invalidate(traceId);
        }
    }
}
