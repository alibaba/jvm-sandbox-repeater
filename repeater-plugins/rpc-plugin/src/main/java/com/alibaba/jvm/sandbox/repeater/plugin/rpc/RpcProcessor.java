package com.alibaba.jvm.sandbox.repeater.plugin.rpc;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.rpc.wrapper.RpcWrapperTransModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * @author qiyi-wangyeran/fanxiuping
 */
class RpcProcessor extends DefaultInvocationProcessor {
    private final static Logger log = LoggerFactory.getLogger(RpcProcessor.class);
    public static ThreadLocal<Object > threadLocalResult = new ThreadLocal<>();

    RpcProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        RpcWrapperTransModel wtm = RpcWrapperTransModel.build(event);
        try {
            if (wtm == null) {
                return new Identity(InvokeType.RPC.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
            }
            if (wtm.getRpcCode().startsWith("receiveBase") && StringUtils.isNotBlank(wtm.getThriftProtocol())){
                threadLocalResult.set(event.argumentArray[0]);
            }
            Map<String, String> extra = new HashMap<String, String>();
            extra.put("Trotocol", wtm.getThriftProtocol());
            return new Identity(InvokeType.RPC.name(),wtm.getThriftProtocol(), wtm.getthriftParameterTypes(), extra);
        } catch (Exception e) {
            log.error("这里是thrift assembleIdentity error:{}",e);
            return new Identity(InvokeType.RPC.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        RpcWrapperTransModel wtm = RpcWrapperTransModel.build(event);
            return new Object[]{wtm};
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN  ) {
            try {
                Object threadLocalValue  = threadLocalResult.get();
                threadLocalResult.remove();
                return threadLocalValue;
            } catch (Exception e) {
                log.error("这里是thrift assembleResponse error:{}", e);
            }
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        Object[] argumentarray=(Object[])event.argumentArray;
        if (argumentarray != null && argumentarray.length >= 2){
            try {
                if ("receiveBase".equals(event.javaMethodName) && invocation.getResponse() != null){
                    if (invocation.getResponse() != null && StringUtils.isNotBlank(invocation.getResponse().toString())){
                        event.argumentArray[0] = invocation.getResponse();
                        invocation.getRequest()[0] = invocation.getResponse();
                    }
                }
                return  null;
            }   catch (Exception e) {
                log.error("error occurred when assemble thrift mock response:{}", e);
                return null;
            }
        }else{
            log.info("rpc thrift assemble mockresponse size:{}", argumentarray.length);
        }
        return null;
    }

}
