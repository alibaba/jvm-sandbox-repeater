package com.alibaba.jvm.sandbox.repeater.plugin.java;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.MethodSignatureParser;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Method;

/**
 * Java类型入口回放器；在sandbox两种挂载模式下工作条件不同（因为无法获取到运行实例）
 * <p>
 * agent启动 ：能够回放spring容器中的任何bean实例
 * <p>
 * attach启动：需要引入repeater-client并在spring中注入{@code SpringContextAware}
 *
 * or
 *
 * 兜底逻辑会使用{@link JavaInstanceCache} 进行实例获取
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class JavaRepeater extends AbstractRepeater {

    private static SerializerProvider provider = SerializerProvider.instance();

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!getType().equals(invocation.getType())) {
            throw new RepeatException("invoke type miss match, required invoke type is: " + invocation.getType());
        }

        ClassLoader classLoader = null;

        Identity identity = invocation.getIdentity();
        Object bean = SpringContextAdapter.getBeanByType(identity.getLocation());

        if (bean == null) {
            bean = JavaInstanceCache.getInstance(identity.getLocation());
        } else {
            classLoader = bean.getClass().getClassLoader();
        }
        if (bean == null) {
            throw new RepeatException("no bean found in context, className=" + identity.getLocation());
        }
        String[] array = identity.getEndpoint().split("~");
        // array[0]=/methodName
        String methodName = array[0].substring(1);

        if (classLoader == null) {
            classLoader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        // fix issue#9 int.class基本类型被解析成包装类型，通过java方法签名来规避这类问题
        // array[1]=javaMethodDesc
        MethodSignatureParser.MethodSpec methodSpec = MethodSignatureParser.parseIdentifier(array[1]);
        Class<?>[] parameterTypes = MethodSignatureParser.loadClass(methodSpec.getParamIdentifiers(), classLoader);
//        Method method = bean.getClass().getDeclaredMethod(methodName, parameterTypes);
//        if (!method.isAccessible()) {
//            method.setAccessible(true);
//        }
        // 开始invoke
        try {
            context.setCanMockDate(true);
            return MethodUtils.invokeMethod(bean, true, methodName, invocation.getRequest(), parameterTypes);
        } catch (Exception e) {
            Serializer serializer = SerializerWrapper.getSerializer(invocation.getSerializeType());
            context.setThrowableSerialized(serializer
                    .serialize2String(invocation.getThrowable(), classLoader));

            throw e;
        } finally {
            context.setCanMockDate(false);
        }
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA;
    }

    @Override
    public String identity() {
        return "java";
    }
}
