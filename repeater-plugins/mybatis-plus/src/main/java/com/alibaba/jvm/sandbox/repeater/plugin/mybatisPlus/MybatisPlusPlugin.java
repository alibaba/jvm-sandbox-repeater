package com.alibaba.jvm.sandbox.repeater.plugin.mybatisPlus;

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
 * @author peng.hu1
 * @Date 2023/4/17 09:51
 */
@MetaInfServices(InvokePlugin.class)
public class MybatisPlusPlugin extends AbstractInvokePluginAdapter {

    @Override
    public InvokeType getType() {
        return InvokeType.MYBATIS_PLUS;
    }

    @Override
    public String identity() {
        return InvokeType.MYBATIS_PLUS.name();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("com.baomidou.mybatisplus.core.override.MybatisMapperMethod")
                .methodPatterns(EnhanceModel.MethodPattern.transform("execute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MybatisPlusProcessor(InvokeType.MYBATIS_PLUS);
    }
}
