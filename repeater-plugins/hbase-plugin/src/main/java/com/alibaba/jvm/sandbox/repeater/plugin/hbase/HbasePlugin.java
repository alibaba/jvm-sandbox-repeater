package com.alibaba.jvm.sandbox.repeater.plugin.hbase;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.google.common.collect.Lists;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * @description:
 * @author: ldb
 * @date: 2021-12-08
 **/
@MetaInfServices(InvokePlugin.class)
public class HbasePlugin extends AbstractInvokePluginAdapter {

    @Override
    public InvokeType getType() {
        return InvokeType.HBASE;
    }

    @Override
    public String identity() {
        return "hbase";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder().classPattern("org.apache.hadoop.hbase.ipc.AbstractRpcClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("callBlockingMethod"))
                .includeSubClasses(true)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS).build();

        EnhanceModel em1 = EnhanceModel.builder().classPattern("org.apache.hadoop.hbase.shaded.protobuf.ResponseConverter")
                .methodPatterns(EnhanceModel.MethodPattern.transform("getResults"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS).build();

        return Lists.newArrayList(em, em1);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new HbaseProcessor(getType());
    }
}
