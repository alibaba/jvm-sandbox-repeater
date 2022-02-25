package com.alibaba.jvm.sandbox.repeater.plugin.thrift;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.thrift.wrapper.ThriftWrapperTransModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * @author wangyeran/fanxiuping
 */
class ThriftProcessor extends DefaultInvocationProcessor {
    private final static Logger log = LoggerFactory.getLogger(ThriftProcessor.class);

    public static ThreadLocal<Object> threadLocalResult = new ThreadLocal<>();

    ThriftProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {

        ThriftWrapperTransModel wtm = ThriftWrapperTransModel.build(event);

        if (wtm == null) {
            return new Identity(InvokeType.THRIFT.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
        if (wtm.getThriftCode().startsWith("receiveBase") && StringUtils.isNotBlank(wtm.getThriftProtocol())) {
            threadLocalResult.set(event.argumentArray[0]);
        }
        Map<String, String> extra = new HashMap<String, String>();
        extra.put("Trotocol", wtm.getThriftProtocol());
        return new Identity(InvokeType.THRIFT.name(), wtm.getThriftCode(), wtm.getThriftParameterTypes(), extra);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        ThriftWrapperTransModel wtm = ThriftWrapperTransModel.build(event);
        return new Object[]{wtm};
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            //response的时候，再将threadLocal里面的数据弹出
            Object threadLocalValue = threadLocalResult.get();
            //这个其实是receiveBase的参数，但是拿不到returnEvent拿不到参数信息，因此先放到response中
            threadLocalResult.remove();
            return threadLocalValue;
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        Object[] argumentarray = (Object[]) event.argumentArray;
        if (argumentarray != null && argumentarray.length >= 2) {
            if ("receiveBase".equals(event.javaMethodName) && invocation.getResponse() != null) {
                log.info("rpc thrift assemble mock response:{}", invocation.getResponse());
                if (invocation.getResponse() != null && StringUtils.isNotBlank(invocation.getResponse().toString())) {
                    event.argumentArray[0] = invocation.getResponse();
                    invocation.getRequest()[0] = invocation.getResponse();
                }
            }
            return null;
        } else {
            log.info("rpc thrift assemble mockresponse size:{}", argumentarray.length);
        }
        return null;
    }

}
