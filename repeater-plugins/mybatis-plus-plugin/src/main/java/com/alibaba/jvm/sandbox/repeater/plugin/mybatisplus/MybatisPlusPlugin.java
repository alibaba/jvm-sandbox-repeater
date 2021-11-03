package com.alibaba.jvm.sandbox.repeater.plugin.mybatisplus;

import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * @Author: luwenrong@zhongan.com
 * @Title:  mybatisplus 插件
 * @Description: {@code com.baomidou.mybatisplus.core.override}包下面的MybatisMapperMethod实现类
 * @Date: 2021/10/26
 */
@MetaInfServices(InvokePlugin.class)
public class MybatisPlusPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("com.baomidou.mybatisplus.core.override.MybatisMapperMethod")
                .methodPatterns(EnhanceModel.MethodPattern.transform("execute"))
                .watchTypes(Type.BEFORE, Type.RETURN, Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MybatisPlusProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MYBATISPLUS;
    }

    @Override
    public String identity() {
        return "mybatis-plus";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

}
