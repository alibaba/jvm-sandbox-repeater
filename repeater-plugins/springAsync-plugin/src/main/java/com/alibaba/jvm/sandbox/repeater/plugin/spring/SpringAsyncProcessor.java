package com.alibaba.jvm.sandbox.repeater.plugin.spring;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.aopalliance.intercept.MethodInvocation;
import java.lang.reflect.Method;

/**
 * 解决spring异步线程的问题
 * @author peng.hu1
 * @Date 2023/3/9 17:58
 */
public class SpringAsyncProcessor extends DefaultInvocationProcessor {

    public SpringAsyncProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object[] objects = event.argumentArray;
        MethodInvocation invocation = (MethodInvocation) objects[0];
        Method method = invocation.getMethod();
        String methodDesc = method.toString();
        String[] array = methodDesc.split(" ");
        String url = String.format("%s://%s", InvokeType.SPRING_ASYNC.name(), array[array.length-1]);
        return new Identity(url);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object[] objects = event.argumentArray;
        MethodInvocation invocation = (MethodInvocation) objects[0];

        return invocation.getArguments();
    }

}
