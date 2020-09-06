package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * {@link DubboConsumerInvocationProcessor}
 * <p>
 * dubbo consumer调用处理器，需要重写组装identity 和 组装request
 * </p>
 *
 * @author zhaoyb1990
 */
class DubboConsumerInvocationProcessor extends DefaultInvocationProcessor {

    DubboConsumerInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object invoker;
        Object invocation;
        // for repeater identity assemble
        // invoke(Invoker<?> invoker, Invocation invocation)
        invoker = event.argumentArray[0];
        invocation = event.argumentArray[1];

        try {
            // methodName
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
            Class<?>[] parameterTypes = (Class<?>[]) MethodUtils.invokeMethod(invocation, "getParameterTypes");

            //  兼容泛化调用
            String[] genericParameterTypes = null;
            if (methodName.equals("$invoke")) {
                Object[] arguments = (Object[]) MethodUtils.invokeMethod(invocation, "getArguments");
                methodName = (String) arguments[0];
                genericParameterTypes = (String[]) arguments[1];
                LogUtil.info("dubbo event listener, parameterTypes:{}", genericParameterTypes);
            }


            // interfaceName
            String interfaceName = ((Class) MethodUtils.invokeMethod(invoker, "getInterface")).getCanonicalName();

            String methodDesc = null;
            if (genericParameterTypes != null) {
                methodDesc = getMethodDesc(methodName, genericParameterTypes);
            } else {
                methodDesc = getMethodDesc(methodName, parameterTypes);
            }
            LogUtil.info("dubbo event listener, methodDesc: {}", methodDesc);
            return new Identity(InvokeType.DUBBO.name(), interfaceName, methodDesc, getExtra());
        } catch (Exception e) {
            // ignore
            LogUtil.error("error occurred when assemble dubbo request", e);
        }
        return new Identity(InvokeType.DUBBO.name(), "unknown", "unknown", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object invocation;
        // for repeater parameter assemble
        // invoke(Invoker<?> invoker, Invocation invocation)
        invocation = event.argumentArray[1];
        try {
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");

            // 兼容泛化调用
            if (methodName.equals("$invoke")) {
                Object[] arguments = (Object[]) MethodUtils.invokeMethod(invocation, "getArguments");
                return (Object[]) arguments[2];
            }
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
        // create by huqiang 2020-09-06 统一使用invoke方法 Result invoke(Invoker<?> invoker, Invocation invocation)
        if (event.type == Event.Type.RETURN) {
            Object result = ((ReturnEvent) event).object;
            try {
                return MethodUtils.invokeMethod(result, "getValue");
            } catch (Exception e) {
                // ignore
                LogUtil.error("error occurred when assemble dubbo response", e);
            }
        }
        return null;
    }
}
