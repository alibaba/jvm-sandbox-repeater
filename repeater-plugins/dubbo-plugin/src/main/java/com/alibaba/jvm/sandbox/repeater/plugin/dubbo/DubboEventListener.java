package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.List;
import java.util.Map;

/**
 * {@link DubboEventListener}
 * <p>
 *
 * @author zhaoyb1990
 */
public class DubboEventListener extends DefaultEventListener {

    private static final String INVOKE = "$invoke";

    public DubboEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }
    @Override
    protected void initContext(Event event) {
        if (event.type == Event.Type.BEFORE && !RepeatCache.isRepeatFlow()) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            if (log.isDebugEnabled()) {
                log.debug("[ dubbo event listener ] initContext, argument {} ", JSON.toJSONString(beforeEvent.argumentArray, SerializerFeature.WriteClassName));
            }
            Object invocation = beforeEvent.argumentArray[1];
            try {
                // 回放流量时会跨线程，所以需要将traceId传递过来
                Map<String, String> attachments = (Map<String, String>) MethodUtils.invokeMethod(invocation, "getAttachments");
                String traceIdX = attachments.get(Constants.HEADER_TRACE_ID_X);
                if (TraceGenerator.isValid(traceIdX)) {
                    Tracer.start(traceIdX);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.initContext(event);
    }

    @Override
    protected Invocation initInvocation(BeforeEvent event) {
        DubboInvocation dubboInvocation = new DubboInvocation();
        //Result invoke(Invoker<?> invoker, Invocation invocation)
        Object invoker = event.argumentArray[0];
        Object invocation = event.argumentArray[1];
        try {
            Object url = MethodUtils.invokeMethod(invoker, "getUrl");
            @SuppressWarnings("unchecked")
            Map<String, String> parameters = (Map<String, String>) MethodUtils.invokeMethod(url, "getParameters");
            String protocol =  (String)MethodUtils.invokeMethod(url, "getProtocol");
            // methodName
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
            Class<?>[] parameterTypes = (Class<?>[]) MethodUtils.invokeMethod(invocation, "getParameterTypes");

            // 兼容泛化调用 create by huqiang 2020-09-03
            String[] genericParameterTypes = null;
            if (methodName.equals(INVOKE)) {
                Object[] arguments = (Object[]) MethodUtils.invokeMethod(invocation, "getArguments");
                methodName = (String) arguments[0];
                genericParameterTypes = (String[]) arguments[1];
            }
            // interfaceName
            String interfaceName = ((Class) MethodUtils.invokeMethod(invoker, "getInterface")).getCanonicalName();
            dubboInvocation.setProtocol(protocol);
            dubboInvocation.setInterfaceName(interfaceName);
            dubboInvocation.setMethodName(methodName);
            dubboInvocation.setParameters(parameters);
            dubboInvocation.setVersion(parameters.get("version"));
            if (genericParameterTypes != null) {
                dubboInvocation.setParameterTypes(genericParameterTypes);
            } else {
                dubboInvocation.setParameterTypes(transformClass(parameterTypes));
            }
            // todo find a right way to get address and group
        } catch (Exception e) {
            LogUtil.error("error occurred when init dubbo invocation", e);
        }
        return dubboInvocation;
    }

    private String[] transformClass(Class<?>[] parameterTypes) {
        List<String> paramTypes = Lists.newArrayList();
        if (ArrayUtils.isNotEmpty(parameterTypes)) {
            for (Class<?> clazz : parameterTypes) {
                paramTypes.add(clazz.getCanonicalName());
            }
        }
        return paramTypes.toArray(new String[0]);
    }
}
