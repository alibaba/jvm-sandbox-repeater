package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * {@link RedisPlugin} redis的java插件
 * <p>
 * TODO 拦截{@code redis.clients.jedis.commands}包下面的commands实现类
 * </p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class RedisPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        return null;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RedisProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.REDIS;
    }

    @Override
    public String identity() {
        return "redis";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
