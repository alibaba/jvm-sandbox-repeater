package com.alibaba.jvm.sandbox.repeater.plugin.jpa;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * {@link SpringDataJpaPlugin} Hibernate插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class SpringDataJpaPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern[] methodPatterns = EnhanceModel.MethodPattern.transform(
                // create
                "save",
                // update
                "saveAndFlush",
                // retrieve
                "get",
                "getOne",
                "findOne",
                "findAll",
                "count",
                "exists",
                // delete
                "delete"
        );
        EnhanceModel enhanceModel = EnhanceModel.builder()
                .classPattern("org.springframework.data.jpa.repository.support.SimpleJpaRepository")
                .methodPatterns(methodPatterns)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SpringDataJpaProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JPA;
    }

    @Override
    public String identity() {
        return "spring-data-jpa-plugin";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
