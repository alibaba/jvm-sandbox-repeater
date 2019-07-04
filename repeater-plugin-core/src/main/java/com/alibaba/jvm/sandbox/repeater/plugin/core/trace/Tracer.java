package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Tracer} 应用内部全局跟踪能力
 * <p>
 * 非常核心的能力；全局的信息需要利用它来串联
 * <p>
 * 如果不开启{@link RepeaterConfig#useTtl}，只能录制到单线程的子调用信息
 * <p>
 * 由于上下文信息是从entrance插件开启{@code Tracer.start()}，必须在entrance插件进行关闭({@code Tracer.end()})，否则会出现上下文错乱问题
 * </p>
 *
 * @author zhaoyb1990
 * @since 1.0.0
 */
public class Tracer {

    private final static Logger log = LoggerFactory.getLogger(Tracer.class);

    private static ThreadLocal<TraceContext> ttlContext = new TransmittableThreadLocal<TraceContext>();

    private static ThreadLocal<TraceContext> normalContext = new ThreadLocal<TraceContext>();

    /**
     * 开启追踪一次调用，非线程安全
     *
     * @return 调用上下文
     */
    public static TraceContext start() {
        return start(null);
    }

    /**
     * 开启追踪一次调用，非线程安全
     *
     * @param traceId 调用唯一
     * @return 调用上下文
     */
    public static TraceContext start(String traceId) {
        TraceContext context = Tracer.getContextCarrie().get();
        if (context != null) {
            return context;
        }
        if (!TraceGenerator.isValid(traceId)) {
            traceId = TraceGenerator.generate();
        }
        context = new TraceContext(traceId);
        if(log.isDebugEnabled()){
            log.debug("[Tracer] start trace success,traceId={},timestamp={}", context.getTraceId(), context.getTimestamp());
        }
        Tracer.getContextCarrie().set(context);
        return context;
    }

    /**
     * 获取当前上下文
     *
     * @return TraceContext
     */
    public static TraceContext getContext() {
        return Tracer.getContextCarrie().get();
    }

    /**
     * 获取当前上下文的追踪ID，未开启追踪情况下返回空
     *
     * @return 调用追踪ID
     */
    public static String getTraceId() {
        return Tracer.getContextCarrie().get() == null ? null : Tracer.getContextCarrie().get().getTraceId();
    }

    /**
     * 结束追踪一次调用，清理上下文
     */
    public static void end() {
        final TraceContext context = getContext();
        if (context != null && log.isDebugEnabled()) {
            log.debug("[Tracer] stop  trace success,type={},traceId={},cost={}ms", context.getInvokeType(), context.getTraceId(), System.currentTimeMillis() - context.getTimestamp());
        }
        getContextCarrie().remove();
    }

    /**
     * 根据用户是否开启ttl选择合适的载体
     *
     * @return 上下文threadLocal载体
     * @see com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig#useTtl
     */
    private static ThreadLocal<TraceContext> getContextCarrie() {
        return ApplicationModel.instance().getConfig().isUseTtl() ? ttlContext : normalContext;
    }
}
