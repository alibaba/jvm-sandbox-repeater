package com.alibaba.jvm.sandbox.repeater.plugin.rpc;

import java.util.List;

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

/**
 * <p>
 *
 * @author qiyi-wangyeran/fanxiuping
 */
@MetaInfServices(InvokePlugin.class)
public class RpcPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern mpsend = EnhanceModel.MethodPattern.builder()
                .methodName("sendBase")
                .parameterType(new String[]{"java.lang.String","org.apache.thrift.TBase","byte"})
                .build();
        EnhanceModel emsend = EnhanceModel.builder()
                .classPattern("org.apache.thrift.TServiceClient")
                .methodPatterns(new EnhanceModel.MethodPattern[]{mpsend})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel emreceive = EnhanceModel.builder()
                .classPattern("org.apache.thrift.TServiceClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("receiveBase"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(emsend, emreceive);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RpcProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.RPC;
    }

    @Override
    public String identity() {
        return "rpc";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new RpcListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

}
