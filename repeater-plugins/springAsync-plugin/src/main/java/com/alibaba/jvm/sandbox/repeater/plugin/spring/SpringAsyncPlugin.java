package com.alibaba.jvm.sandbox.repeater.plugin.spring;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/9 17:46
 */
@MetaInfServices(InvokePlugin.class)
public class SpringAsyncPlugin extends AbstractInvokePluginAdapter {

    @Override
    public InvokeType getType() {
        return InvokeType.SPRING_ASYNC;
    }

    @Override
    public String identity() {
        return InvokeType.SPRING_ASYNC.name();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder()
                .classPattern("org.springframework.aop.interceptor.AsyncExecutionInterceptor")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "invoke"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Arrays.asList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SpringAsyncProcessor(getType());
    }
}
