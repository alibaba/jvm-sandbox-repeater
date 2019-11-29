package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import java.util.concurrent.Callable;

import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceAdapterListener;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;


/**
 * {@link TtlConcurrentAdvice} {@code TransmittableThreadLocal} 多线程适配
 *
 * <p>
 * {@link com.alibaba.ttl.TransmittableThreadLocal}对线程上下文进行拷贝要求使用{@link com.alibaba.ttl.TtlRunnable} or {@link
 * com.alibaba.ttl.TtlCallable}
 *
 * 对于动态attach的模块，无法保障业务使用这个能力，ttl提供了{@link com.alibaba.ttl.threadpool.agent.TtlAgent}动态包装;
 *
 * {@link TtlConcurrentAdvice} 提供了类似的思路，基于jvm-sandbox的动态编织能力，对concurrent包下的并发处理器进行增强，包装成Ttl类
 *
 * </p>
 *
 * @author zhaoyb1990
 *
 * 适配了ThreadPoolExecutor/ScheduledThreadPoolExecutor; ForkJoinTask/ForkJoinPool没有适配
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ThreadPoolExecutor
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * @see java.util.concurrent.Executors
 */
public class TtlConcurrentAdvice {

    private final ModuleEventWatcher watcher;

    private TtlConcurrentAdvice(ModuleEventWatcher watcher) {
        this.watcher = watcher;
    }

    public static TtlConcurrentAdvice watcher(ModuleEventWatcher watcher) {
        return new TtlConcurrentAdvice(watcher);
    }

    public synchronized void watch(RepeaterConfig config) {
        if (config != null && config.isUseTtl()) {
            new EventWatchBuilder(watcher)
                .onClass("java.util.concurrent.ThreadPoolExecutor").includeBootstrap()
                .onBehavior("execute")
                .onBehavior("submit")
                .onClass("java.util.concurrent.ScheduledThreadPoolExecutor").includeBootstrap()
                .onBehavior("execute")
                .onBehavior("submit")
                .onBehavior("schedule")
                .onBehavior("scheduleAtFixedRate")
                .onBehavior("scheduleWithFixedDelay")
                .onWatch(new AdviceAdapterListener(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        // 包装成ttl
                        final Object[] parameterArray = advice.getParameterArray();
                        final Class<?>[] parameterTypeArray = advice.getBehavior().getParameterTypes();
                        if (parameterArray == null || parameterArray.length < 1) {return;}
                        if (parameterArray[0] instanceof com.alibaba.ttl.TtlEnhanced) {return;}
                        Class<?> parameter0Type = parameterTypeArray[0];
                        if (parameter0Type.isAssignableFrom(Runnable.class)) {
                            parameterArray[0] = TtlRunnable.get((Runnable) parameterArray[0]);
                        }
                        if (parameter0Type.isAssignableFrom(Callable.class)) {
                            parameterArray[0] = TtlCallable.get((Callable) parameterArray[0]);
                        }
                    }
                    // fix issue #41
                }), Type.BEFORE, Type.RETURN, Type.THROWS);
        }
    }
}
