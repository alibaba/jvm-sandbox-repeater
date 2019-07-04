package com.alibaba.jvm.sandbox.repeater.plugin.java;

import java.lang.reflect.Method;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.ClassUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;

import org.kohsuke.MetaInfServices;

/**
 * Java类型入口回放器；在sandbox两种挂载模式下工作条件不同
 *
 * agent启动 ：能够回放spring容器中的任何bean实例
 *
 * attach启动：需要引入repeater-client并在spring中注入{@code SpringContextAware}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class JavaRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        Identity identity = invocation.getIdentity();
        Object bean = SpringContextAdapter.getBeanByType(identity.getLocation());
        if (bean == null) {
            throw new RepeatException("no bean found in context, className=" + identity.getLocation());
        }
        if (invocation.getType() != getType()) {
            throw new RepeatException("invoke type miss match, required invoke type is: " + invocation.getType());
        }
        String[] array = identity.getEndpoint().split("~");
        // array[0]=/methodName
        String methodName = array[0].substring(1);
        // 根据入参转换parameterTypes，入参是基本类型(int.class)则会找到不方法，原因是toClass只会返回包装类型
        Class<?>[] parameterTypes = ClassUtils.toClass(invocation.getRequest());
        Method method = bean.getClass().getDeclaredMethod(methodName, parameterTypes);
        // 开始invoke
        return method.invoke(bean, invocation.getRequest());
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
