package com.alibaba.jvm.sandbox.repeater.plugin.hibernate;

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
 * {@link HibernatePlugin} Hibernate插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class HibernatePlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern[] methodPatterns = EnhanceModel.MethodPattern.transform(
                // create
                "save",
                "saveOrUpdate",
                "persist",
                "persistOnFlush",
                "replicate",
                // update
                "update",
                "merge",
                "executeUpdate",
                // retrieve
                "load",
                "get",
                "list",
                // delete
                "delete"
        );
        EnhanceModel version3DotX = EnhanceModel.builder()
                .classPattern("org.hibernate.impl.SessionImpl")
                .methodPatterns(methodPatterns)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel version4DotX = EnhanceModel.builder()
                .classPattern("org.hibernate.internal.SessionImpl")
                .methodPatterns(methodPatterns)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(version3DotX, version4DotX);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new HibernateProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HIBERNATE;
    }

    @Override
    public String identity() {
        return "hibernate-plugin";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
