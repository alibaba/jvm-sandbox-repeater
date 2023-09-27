package com.alibaba.jvm.sandbox.repeater.plugin.caffeine;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel.MethodPattern;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.MetaInfServices;


/**
 * CaffeineCachePlugin - 缓存插件
 *
 * @author vivo-钱兆良
 * @version 1.0
 * @CreateDate: 2020/11/5 17:38
 */
@MetaInfServices(InvokePlugin.class)
public class CaffeineCachePlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        MethodPattern[] methodPatterns = MethodPattern.transform("getIfPresent","get","getAllPresent","getAll", "asMap");
        String[] classArray = {"com.github.benmanes.caffeine.cache.LocalManualCache"
        , "com.github.benmanes.caffeine.cache.LocalLoadingCache"
                , "com.github.benmanes.caffeine.cache.LocalAsyncCache"
                , "com.github.benmanes.caffeine.cache.LocalAsyncLoadingCache"};
        List<EnhanceModel> enhanceModels = new ArrayList<EnhanceModel>(classArray.length);
        for (String classPattern : classArray) {
            enhanceModels.add(
                    EnhanceModel.builder().classPattern(classPattern)
                            .methodPatterns(methodPatterns)
                            .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                            .build()
            );
        }
        return enhanceModels;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new CaffeineCacheInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.CAFFEINE_CACHE;
    }

    @Override
    public String identity() {
        return "caffeine-cache";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
