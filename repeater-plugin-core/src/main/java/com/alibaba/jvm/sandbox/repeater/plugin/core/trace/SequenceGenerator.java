package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <p>
 * 序列化生成器，利用atomic原子性保证子调用序列在{@link TraceContext#getTraceId()}下唯一
 *
 * @author zhaoyb1990
 */
public class SequenceGenerator {

    private static final LoadingCache<String, AtomicInteger> SEQUENCE_CACHE = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(4096L)
        .build(new CacheLoader<String, AtomicInteger>() {
            @Override
            public AtomicInteger load(String traceId) throws Exception {
                return new AtomicInteger(0);
            }
        });

    /**
     * 生成序列: 根据traceId生成sequence序列
     *
     * @param traceId traceId
     * @return 生成的sequence
     */
    public static Integer generate(String traceId) {
        try {
            return SEQUENCE_CACHE.get(traceId).incrementAndGet();
        } catch (Exception e) {
            // impossible and ignore this
            return -1;
        }
    }

    /**
     * 读取当前sequence;
     *
     * @param traceId traceId
     * @return 当前的sequence
     */
    public static Integer read(String traceId) {
        try {
            return SEQUENCE_CACHE.get(traceId).get();
        } catch (Exception e) {
            // impossible and ignore this
            return -1;
        }
    }

    /**
     * 回滚序列: 根据traceId做索引
     *
     * @param traceId traceID
     * @return 回滚后sequence
     */
    public static Integer rollback(String traceId) {
        try {
            return SEQUENCE_CACHE.get(traceId).decrementAndGet();
        } catch (Exception e) {
            // impossible and ignore this
            return -1;
        }
    }

    /**
     * 失效序列
     *
     * @param traceId traceID
     */
    public static void invalid(String traceId) {
        try {
            SEQUENCE_CACHE.invalidate(traceId);
        } catch (Exception e) {
            // impossible and ignore this
        }
    }
}
