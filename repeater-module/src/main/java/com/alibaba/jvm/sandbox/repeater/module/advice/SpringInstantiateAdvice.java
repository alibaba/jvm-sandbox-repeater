package com.alibaba.jvm.sandbox.repeater.module.advice;

import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceAdapterListener;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextInnerContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SpringInstantiateAdvice} spring初始化拦截器，agent启动模式下拦截记录beanName和bean，用作回放
 * <p>
 *
 * @author zhaoyb1990
 */
public class SpringInstantiateAdvice {

    private final static Logger log = LoggerFactory.getLogger(SpringInstantiateAdvice.class);

    private final ModuleEventWatcher watcher;

    private SpringInstantiateAdvice(ModuleEventWatcher watcher) {
        this.watcher = watcher;
    }

    public static SpringInstantiateAdvice watcher(ModuleEventWatcher watcher) {
        return new SpringInstantiateAdvice(watcher);
    }

    public synchronized void watch() {
        new EventWatchBuilder(watcher)
            .onClass("org.springframework.beans.factory.support.SimpleInstantiationStrategy")
            .onBehavior("instantiate")
            .onWatch(new AdviceAdapterListener(new AdviceListener() {
                @Override
                protected void afterReturning(Advice advice) throws Throwable {
                    try {
                        /* (RootBeanDefinition beanDefinition, String beanName...) */
                        String beanName = advice.getParameterArray()[1].toString();
                        Object target = advice.getReturnObj();
                        SpringContextInnerContainer.addBean(beanName, advice.getReturnObj());
                        log.info("Register bean:name={},instance={}", beanName, target);
                    } catch (Exception e) {
                        log.error("[Error-2000]-register spring bean occurred error.", e);
                    }
                }
            }), Type.BEFORE, Type.RETURN);
    }
}
