package com.alibaba.jvm.sandbox.repeater.plugin.guava;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;

/**
 * GuavaCacheInvocationProcessor - guava cache处理插件
 *
 * @author vivo-刘延江
 * @version 1.0
 * @CreateDate: 2020/11/24 15:58
 */
public class GuavaCacheInvocationProcessor extends DefaultInvocationProcessor {
    public GuavaCacheInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        //这么做是为了防止V get(K key, Callable<? extends V> valueLoader) 这种所以取第一个
        Object[] eventArray =event.argumentArray;
        return new Identity(getType().name(), event.javaClassName, event.javaMethodName  + ParameterTypesUtil.getTypesStrByObjects(new Object[]{eventArray[0]}), getExtra());
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if(event.argumentArray !=null && event.argumentArray.length >1){
            return new Object[]{event.argumentArray[0]};
        }
        return event.argumentArray;
    }
}
