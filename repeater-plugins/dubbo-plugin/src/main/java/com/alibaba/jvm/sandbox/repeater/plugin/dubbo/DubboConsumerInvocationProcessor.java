package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link DubboConsumerInvocationProcessor}
 * <p>
 * dubbo consumer调用处理器，需要重写组装identity 和 组装request
 * </p>
 *
 * @author zhaoyb1990
 */
class DubboConsumerInvocationProcessor extends DefaultInvocationProcessor {

    private static final String ON_RESPONSE = "onResponse";

    private static final String INVOKE = "invoke";

    private Set<Integer> ignoreInvokeSet = new HashSet<Integer>(128);

    DubboConsumerInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object invoker;
        Object invocation;
        if (ON_RESPONSE.equals(event.javaMethodName)) {
            // for record identity assemble
            // onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {}
            invoker = event.argumentArray[1];
            invocation = event.argumentArray[2];
        } else {
            // for repeater identity assemble
            // invoke(Invoker<?> invoker, Invocation invocation)
            invoker = event.argumentArray[0];
            invocation = event.argumentArray[1];
        }

        try {
            // methodName
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
            Class<?>[] parameterTypes = (Class<?>[]) MethodUtils.invokeMethod(invocation, "getParameterTypes");
            // interfaceName
            String  interfaceName = ((Class)MethodUtils.invokeMethod(invoker, "getInterface")).getCanonicalName();
            return new Identity(InvokeType.DUBBO.name(), interfaceName, getMethodDesc(methodName, parameterTypes), getExtra());
        } catch (Exception e) {
            // ignore
            LogUtil.error("error occurred when assemble dubbo request", e);
        }
        return new Identity(InvokeType.DUBBO.name(), "unknown", "unknown", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object invocation;
        if (ON_RESPONSE.equals(event.javaMethodName)) {
            // for record parameter assemble
            // onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {}
            invocation = event.argumentArray[2];
        } else {
            // for repeater parameter assemble
            // invoke(Invoker<?> invoker, Invocation invocation)
            invocation = event.argumentArray[1];
        }
        try {
            return (Object[]) MethodUtils.invokeMethod(invocation, "getArguments");
        } catch (Exception e) {
            // ignore
            LogUtil.error("error occurred when assemble dubbo request", e);
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        // Result invoke(Invoker<?> invoker, Invocation invocation)
        try {
            Object dubboInvocation = event.argumentArray[1];
            Object response = invocation.getResponse();
            Class<?> aClass = event.javaClassLoader.loadClass("org.apache.dubbo.rpc.AsyncRpcResult");
            // 调用AsyncRpcResult#newDefaultAsyncResult返回;
            return MethodUtils.invokeStaticMethod(aClass, "newDefaultAsyncResult",
                    new Object[]{response, dubboInvocation}, new Class[]{Object.class, dubboInvocation.getClass()});
        } catch (ClassNotFoundException e) {
            LogUtil.error("no valid AsyncRpcResult class fount in classloader {}", event.javaClassLoader, e);
            return null;
        } catch (Exception e) {
            LogUtil.error("error occurred when assemble dubbo mock response", e);
            return null;
        }
    }

    @Override
    public Object assembleResponse(Event event) {
        // 在onResponse的before事件中组装response
        if (event.type == Event.Type.BEFORE) {
            Object appResponse = ((BeforeEvent) event).argumentArray[0];
            try {
                return MethodUtils.invokeMethod(appResponse, "getValue");
            } catch (Exception e) {
                // ignore
                LogUtil.error("error occurred when assemble dubbo response", e);
            }
        }
        return null;
    }

    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent be = (BeforeEvent) event;
            String methodName = be.javaMethodName;
            // 回放流量忽略onResponse，非回放流量忽略invoke方法
            boolean ignore = RepeatCache.isRepeatFlow()
                    ? ON_RESPONSE.equals(methodName)
                    : INVOKE.equals(methodName);
            if (ignore) {
                ignoreInvokeSet.add(be.invokeId);
            }
            return ignore;
        } else {
            return ignoreInvokeSet.remove(event.invokeId);
        }
    }
}
