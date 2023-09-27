package com.alibaba.jvm.sandbox.repeater.plugin.feign;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by jian.xie1 on 2022/11/16
 */
public class FeignDefaultClientProcessor extends DefaultInvocationProcessor {

    public FeignDefaultClientProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object handler = event.target;
        Field metadata_field = FieldUtils.getDeclaredField(handler.getClass(), "metadata", true);

        try {
            Object metadata = metadata_field.get(handler);
            String configKey = (String) MethodUtils.invokeMethod(metadata, "configKey");
            return new Identity(InvokeType.FEIGN_DEFAULT_CLIENT.name(), configKey, "",  null);
        } catch (Exception e) {
            return new Identity(InvokeType.FEIGN_DEFAULT_CLIENT.name(), "Unknown", "Unknown", null);
        }

    }

    @Override
    protected boolean skipMock(BeforeEvent event, Boolean entrance, RepeatContext context) {
        //优先继承父类
        boolean superSKip = super.skipMock(event, entrance, context);
        if (superSKip) {
            return true;
        }

        DynamicConfig dynamicConfig = ApplicationModel.instance().getDynamicConfig();
        Set<String> skipMockIdentities = dynamicConfig.getSkipMockIdentities2();
        Identity identity = assembleIdentity(event);
        if (skipMockIdentities.contains(identity.getUri())) {
            return true;
        }

        return false;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        return invocation.getResponse();
    }

}
