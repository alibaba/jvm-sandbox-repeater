package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;

import org.kohsuke.MetaInfServices;
import redis.clients.jedis.commands.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * {@link RedisPlugin} jedis的java插件
 * <p>
 * 拦截{@code redis.clients.jedis.commands}包下面的commands实现类
 *
 * 获取redis常用操作指令，不包括所有命令
 * 详见Jedis类、BinaryJedis类的实现接口
 * </p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class RedisPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel jedis = EnhanceModel.builder()
                .classPattern("redis.clients.jedis.Jedis")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        getMethodNamesByClass(Lists.<Class>newArrayList(JedisCommands.class, MultiKeyCommands.class, ScriptingCommands.class))
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel binaryJedis = EnhanceModel.builder()
                .classPattern("redis.clients.jedis.BinaryJedis")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        getMethodNamesByClass(Lists.<Class>newArrayList(BinaryJedisCommands.class, MultiKeyBinaryCommands.class, BinaryScriptingCommands.class))
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(jedis, binaryJedis);
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


    /**
     * 传入类的列表，返回列表中的类的所有方法名。
     * @param classes 类列表
     * @return 方法名数组
     */
    private String[] getMethodNamesByClass(List<Class> classes){
        if(classes == null || classes.isEmpty()){
            return new String[0];
        }

        List<Method> methods = new ArrayList();
        for(Class clazz : classes){
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        Set<String> methodNames = new HashSet<String>();
        for(Method method:methods){
            methodNames.add(method.getName());
        }

        return methodNames.toArray(new String[methodNames.size()]);
    }
}
