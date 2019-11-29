package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 通用线程池（非执行单一任务）
 * <p>
 *
 * @author zhaoyb1990
 */
public class ExecutorInner {

    private static ExecutorService executor = new ThreadPoolExecutor( Runtime.getRuntime().availableProcessors() - 1,
        4 * Runtime.getRuntime().availableProcessors(),
        30L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(4096),
        new BasicThreadFactory.Builder().namingPattern("repeater-common-pool-%d").build(),
        new ThreadPoolExecutor.CallerRunsPolicy());

    public static void execute(Runnable r) {
        executor.execute(r);
    }

    public static Executor getExecutor() {
        return executor;
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }
}
