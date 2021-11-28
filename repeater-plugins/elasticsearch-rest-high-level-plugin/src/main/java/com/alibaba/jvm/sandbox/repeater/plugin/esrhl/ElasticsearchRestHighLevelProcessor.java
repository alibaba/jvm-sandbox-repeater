package com.alibaba.jvm.sandbox.repeater.plugin.esrhl;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

/**
 * @Author: luwenrong
 * @Title:  ElasticsearchRestHighLevelProcessor
 * @Date: 2021/10/19
 */
class ElasticsearchRestHighLevelProcessor extends DefaultInvocationProcessor {
    ElasticsearchRestHighLevelProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        //异步方法目前只记录不处理
        if (event.javaMethodName.endsWith("Async")) {
            return new Object[]{};
        }
        return event.argumentArray;
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            ReturnEvent returnEvent = (ReturnEvent) event;
            Object response = returnEvent.object;
            if (response.getClass().getTypeName().equals("org.elasticsearch.client.Cancellable")) {
                return null;
            }
        }
        return super.assembleResponse(event);
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        if (event.javaMethodName.endsWith("Async")) {
            return null;
        }
        return invocation.getResponse();
    }
}
