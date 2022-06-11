package com.alibaba.jvm.sandbox.repeater.plugin.redisson;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;

import org.kohsuke.MetaInfServices;
import java.util.*;

/**
 * {@link RedissonPlugin} redis客户端redisson子插件
 *
 * @author quansong
 * @version 1.0
 */
@MetaInfServices(InvokePlugin.class)
public class RedissonPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel redisson = EnhanceModel.builder()
                .classPattern("org.redisson.Redisson*")
                .methodPatterns(EnhanceModel.MethodPattern.transform("*"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(redisson);
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RedissonProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.REDISSON;
    }

    @Override
    public String identity() {
        return "redisson";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }


    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new RedissonListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
