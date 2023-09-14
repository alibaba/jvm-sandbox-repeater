package com.alibaba.jvm.sandbox.repeater.plugin.springData;

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
public class SpringDataPlugin extends AbstractInvokePluginAdapter {

    @Override
    public InvokeType getType() {
        return InvokeType.SPRING_DATA;
    }

    @Override
    public String identity() {
        return InvokeType.SPRING_DATA.name();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder()
                .classPattern("org.springframework.data.repository.core.support.RepositoryFactorySupport$QueryExecutorMethodInterceptor")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "invoke"
                ))
                .includeSubClasses(true)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Arrays.asList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SpringDataProcessor(getType());
    }
}
