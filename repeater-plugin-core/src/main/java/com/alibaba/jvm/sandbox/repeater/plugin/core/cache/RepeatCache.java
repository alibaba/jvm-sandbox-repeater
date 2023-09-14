package com.alibaba.jvm.sandbox.repeater.plugin.core.cache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link RepeatCache} 回放缓存
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeatCache {

    private static final LoadingCache<String, RepeatContext> CONTEXT_CACHE = CacheBuilder
        .newBuilder()
        .maximumSize(4096)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build(new CacheLoader<String, RepeatContext>() {
            @Override
            public RepeatContext load(String s) throws Exception {
                RepeatMeta meta = new RepeatMeta();
                return new RepeatContext(meta, null, null, false);
            }
        });

    /**
     * 单次回放，避免debug过程中LoadingCache的超时失效策略，而单独设计的
     */
    private static final Map<String, RepeatContext> SINGLE_REPLAY_CACHE = new ConcurrentHashMap<>();

    private static final LoadingCache<String, List<MockInvocation>> MOCK_INVOCATION_CONTEXT = CacheBuilder
        .newBuilder()
        .maximumSize(4096)
        .expireAfterWrite(90, TimeUnit.SECONDS)
        .build(new CacheLoader<String, List<MockInvocation>>() {
            @Override
            public List<MockInvocation> load(String s) throws Exception {
                return Collections.synchronizedList(Lists.newArrayList());
            }
        });

    /**
     * 判断当前请求是否是回放流量
     *
     * 该方法要依赖{@link com.alibaba.jvm.sandbox.repeater.plugin.api.FlowDispatcher#dispatch(RepeatMeta, RecordModel)}中进行回放上下文put
     *
     * @param traceId 请求ID
     * @return 是否回放流量
     */
    public static boolean isRepeatFlow(String traceId) {

        boolean v= StringUtils.isNotEmpty(traceId) && ((CONTEXT_CACHE.getIfPresent(traceId) != null) || SINGLE_REPLAY_CACHE.containsKey(traceId));
        return v;
    }


    /**
     * 判断当前请求是否是回放流量
     *
     * 该方法要依赖{@link com.alibaba.jvm.sandbox.repeater.plugin.api.FlowDispatcher#dispatch(RepeatMeta, RecordModel)}中进行回放上下文put
     *
     * @return 是否回放流量
     */
    public static boolean isRepeatFlow() {
        return StringUtils.isNotEmpty(Tracer.getTraceId()) && ((CONTEXT_CACHE.getIfPresent(Tracer.getTraceId()) != null) || SINGLE_REPLAY_CACHE.containsKey(Tracer.getTraceId()));
    }

    /**
     * 放置回放上下文（一般在回放流量发起之前
     * {@link com.alibaba.jvm.sandbox.repeater.plugin.api.FlowDispatcher#dispatch(RepeatMeta, RecordModel)}
     *
     * @param context 上下文
     */
    public static void putRepeatContext(RepeatContext context) {
        if (context.isSingleReplay()) {
            SINGLE_REPLAY_CACHE.put(context.getTraceId(), context);
        } else {
            CONTEXT_CACHE.put(context.getTraceId(), context);
        }
    }

    /**
     * 获取回放上下文
     *
     * @param traceId 请求ID
     * @return 回放上下文
     */
    public static RepeatContext getRepeatContext(String traceId) {

        if (StringUtils.isEmpty(traceId)) {
            return null;
        }

        RepeatContext context = CONTEXT_CACHE.getIfPresent(traceId);
        if (context!=null) {
            return context;
        }

        if (SINGLE_REPLAY_CACHE.containsKey(traceId)) {
            return SINGLE_REPLAY_CACHE.get(traceId);
        }

        return null;
    }

    public static void addMockInvocation(MockInvocation invocation) {
        try {
            MOCK_INVOCATION_CONTEXT.get(invocation.getTraceId()).add(invocation);
        } catch (ExecutionException e) {
            // impossible
        }
    }

    public static void removeInvocation(String traceId) {
        try {
            MOCK_INVOCATION_CONTEXT.get(traceId).clear();
        } catch (ExecutionException e) {
            // impossible
        }
    }

    public static List<MockInvocation> getMockInvocation(String traceId) {
        return MOCK_INVOCATION_CONTEXT.getIfPresent(traceId);
    }

    public static void removeRepeatContext(String traceId) {
        if (StringUtils.isNotEmpty(traceId)) {
            CONTEXT_CACHE.invalidate(traceId);

            if (SINGLE_REPLAY_CACHE.containsKey(traceId)) {
                SINGLE_REPLAY_CACHE.remove(traceId);
            }
        }
    }
}
