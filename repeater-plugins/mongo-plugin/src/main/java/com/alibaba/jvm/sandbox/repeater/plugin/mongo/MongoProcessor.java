package com.alibaba.jvm.sandbox.repeater.plugin.mongo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.mongo.wrapper.MongoWrapperTransModel;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * <p>
 *
 * @author wangyeran
 */
class MongoProcessor extends DefaultInvocationProcessor {
    protected static Logger log = LoggerFactory.getLogger(MongoProcessor.class);

    MongoProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        try {
            MongoWrapperTransModel wtm = MongoWrapperTransModel.build(event);
            return new Identity(InvokeType.MONGO.name(),wtm.getDbName() + "_" + wtm.gettableName(),wtm.getmethodName() + "_" + wtm.getparamString(), null);
        } catch (Exception e) {
            return new Identity(InvokeType.MONGO.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        // args可能存在不可序序列化异常（例如使用tk.mybatis)
        MongoWrapperTransModel wtm = MongoWrapperTransModel.build(event);
        log.debug("the mongo request:{}, {}", wtm.getDbName() + "_" + wtm.gettableName(), wtm.getmethodName() + "_" + wtm.getparamString());
        return new Object[]{wtm};
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            Object response = ((ReturnEvent) event).object;
            try {
                Object collection = MethodUtils.invokeMethod(response, "getCollection");
                log.debug("here in the record response:{}",collection);
            }catch(Exception e){
                e.printStackTrace();
            }
            return response;
        }
        return null;
    }
}
