package com.alibaba.jvm.sandbox.repeater.plugin.hikv;

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
public class HikvPlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("com.iqiyi.hikv.ToBlockingHiKV")
                .methodPatterns(EnhanceModel.MethodPattern.transform("get"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    protected HikvProcesssor getInvocationProcessor() {
        return new HikvProcesssor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HIKV;
    }

    @Override
    public String identity() {
        return "hikv";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
