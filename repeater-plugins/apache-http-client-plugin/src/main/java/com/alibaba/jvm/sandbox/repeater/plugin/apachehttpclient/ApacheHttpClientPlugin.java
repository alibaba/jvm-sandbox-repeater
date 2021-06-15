package com.alibaba.jvm.sandbox.repeater.plugin.apachehttpclient;

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
 * apache httpClient 插件
 *
 * @Author: vivo-孙道明
 * @version 1.0
 * @CreateDate: 2020/11/5 17:38
 */
@MetaInfServices(InvokePlugin.class)
public class ApacheHttpClientPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("org.apache.http.impl.client.InternalHttpClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("doExecute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ApacheHttpClientProcessor(this.getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.APACHE_HTTP_CLIENT;
    }

    @Override
    public String identity() {
        return "apache-http-client";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
