package com.alibaba.jvm.sandbox.repeater.plugin.socketio;


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
 * {@link SocketIOPlugin} netty-socketio的java插件
 * https://github.com/mrniko/netty-socketio
 * <p>
 * 拦截{@code com.corundumstudio.socketio.namespace.Namespace}包下面的event事件
 * 拦截{@code com.corundumstudio.socketio.transport.NamespaceClient}包下面的调用方法
 * </p>
 *
 * @author xuminwlt
 */
@MetaInfServices(InvokePlugin.class)
public class SocketIOPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel namespace = EnhanceModel.builder()
                .classPattern("com.corundumstudio.socketio.namespace.Namespace")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "onEvent",
                        "onConnect",
                        "onDisconnect"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.THROWS)
                .build();

        EnhanceModel namespaceClient = EnhanceModel.builder()
                .classPattern("com.corundumstudio.socketio.transport.NamespaceClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "joinRoom",
                        "leaveRoom",
                        "sendEvent"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(namespace, namespaceClient);
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SocketIOProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.SOCKETIO;
    }

    @Override
    public String identity() {
        return "socketio";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }


}
