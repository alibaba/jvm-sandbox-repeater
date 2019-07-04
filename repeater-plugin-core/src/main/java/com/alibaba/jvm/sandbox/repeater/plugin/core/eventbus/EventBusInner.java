package com.alibaba.jvm.sandbox.repeater.plugin.core.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.SubscribeEvent;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class EventBusInner {

    private final static Logger log = LoggerFactory.getLogger(EventBusInner.class);

    /**
     * 回放反序列化开销很大，cpu密集，线程池大小设置成核心数 - 1
     */
    private final static ExecutorService executor = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors() - 1,
        2 * Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<Runnable>(4096),
        new BasicThreadFactory.Builder().namingPattern("repeat-task-pool-%d").build(),
        new CallerRunsPolicy());

    private static final EventBus eventBus = new AsyncEventBus("repeater-repeat-executor", executor);

    public static void post(SubscribeEvent event) {
        eventBus.post(event);
    }

    public static void register(Object object, String type) {
        try {
            eventBus.register(object);
            log.info("register event bus success in {}", type);
        } catch (Exception e) {
            log.error("register event bus error in {}", type, e);
        }
    }

    public static void unregister(Object object, String type) {
        try {
            eventBus.unregister(object);
            log.info("unregister event bus success in {}", type);
        } catch (Exception e) {
            log.error("unregister event bus error in {}", type, e);
        }
    }

}
