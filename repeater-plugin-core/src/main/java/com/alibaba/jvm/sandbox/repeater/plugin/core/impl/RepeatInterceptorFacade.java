package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.RepeatInterceptor;

import com.google.common.collect.Lists;

/**
 * {@link RepeatInterceptorFacade}作为{@link RepeatInterceptor}的包装，负责管理和执行所有的SPI实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeatInterceptorFacade implements RepeatInterceptor {

    private static final RepeatInterceptorFacade INSTANCE = new RepeatInterceptorFacade();

    public static RepeatInterceptorFacade instance() {
        return INSTANCE;
    }

    private List<RepeatInterceptor> interceptors = Lists.newArrayList();

    private RepeatInterceptorFacade() {
        ServiceLoader<RepeatInterceptor> sl = ServiceLoader.load(RepeatInterceptor.class, this.getClass().getClassLoader());
        final Iterator<RepeatInterceptor> iterator = sl.iterator();
        while (iterator.hasNext()) {
            interceptors.add(iterator.next());
        }
    }

    @Override
    public void beforeInvoke(RecordModel recordModel) {
        for (RepeatInterceptor interceptor : interceptors) {
            if (interceptor.matchingInvoke(recordModel)) {
                interceptor.beforeInvoke(recordModel);
            }
        }
    }

    @Override
    public void beforeReturn(RecordModel recordModel, Object response) {
        for (RepeatInterceptor interceptor : interceptors) {
            if (interceptor.matchingReturn(recordModel, response)) {
                interceptor.beforeReturn(recordModel, response);
            }
        }
    }

    @Override
    public boolean matchingInvoke(RecordModel recordModel) {
        return false;
    }

    @Override
    public boolean matchingReturn(RecordModel recordModel, Object response) {
        return false;
    }
}
