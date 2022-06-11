package com.alibaba.jvm.sandbox.repeater.plugin.couchbase;


import java.util.List;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;

import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

/**
 * <p>
 *
 * @author wangyeran
 */
@MetaInfServices(InvokePlugin.class)
public class CouchBasePlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em1 = EnhanceModel.builder()
                .classPattern("net.spy.memcached.MemcachedClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("delete"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel em2 = EnhanceModel.builder()
                .classPattern("net.spy.memcached.MemcachedClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("get"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel em3 = EnhanceModel.builder()
                .classPattern("net.spy.memcached.MemcachedClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("getBulk"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em1, em2, em3);
    }

    protected CouchBaseProcessor getInvocationProcessor() {
        return new CouchBaseProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.COUCH_BASE;
    }

    @Override
    public String identity() {
        return "couchbase";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

}
