package com.alibaba.jvm.sandbox.repeater.plugin.thrift;

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
 * @author wangyeran/fanxiuping
 */
@MetaInfServices(InvokePlugin.class)
public class ThriftPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern mpsend = EnhanceModel.MethodPattern.builder()
                .methodName("sendBase")
                .parameterType(new String[]{"java.lang.String", "org.apache.thrift.TBase", "byte"})
                .build();
        EnhanceModel emSend = EnhanceModel.builder()
                .classPattern("org.apache.thrift.TServiceClient")
                .methodPatterns(new EnhanceModel.MethodPattern[]{mpsend})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel emReceive = EnhanceModel.builder()
                .classPattern("org.apache.thrift.TServiceClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("receiveBase"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(emSend, emReceive);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ThriftProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.THRIFT;
    }

    @Override
    public String identity() {
        return "thrift";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new ThriftListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

}
