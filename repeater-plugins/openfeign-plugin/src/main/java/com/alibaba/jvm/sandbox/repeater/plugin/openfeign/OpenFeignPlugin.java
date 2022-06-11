package com.alibaba.jvm.sandbox.repeater.plugin.openfeign;

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
 * @Author: luwenrong
 * @Title: openfeign 插件
 * @Description:
 * @Date: 2021/11/30
 */
@MetaInfServices(InvokePlugin.class)
public class OpenFeignPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("feign.Client$Default")
                .methodPatterns(EnhanceModel.MethodPattern.transform("execute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new OpenFeignProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.OPENFEIGN;
    }

    @Override
    public String identity() {
        return "openfeign";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
