package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.kohsuke.MetaInfServices;


/**
 * {@link DubboRepeater} dubbo回放器
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class DubboRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!(invocation instanceof DubboInvocation)) {
            throw new RepeatException("type miss match, required DubboInvocation but found " + invocation.getClass().getSimpleName());
        }
        DubboInvocation dubboInvocation = (DubboInvocation) invocation;

        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(GenericService.class.getClassLoader());

            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("jvm-sandbox-repeater");
            applicationConfig.setOwner("repeater");
            reference.setApplication(ConfigManager.getInstance().getApplication().orElse(applicationConfig));

            // require address to initialize registry config
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setProtocol(dubboInvocation.getProtocol());

            String address = context.getMeta().getExtension().get("dubbo.address");
            // using special address
            if (StringUtils.isNotEmpty(address)) {
                registryConfig.setAddress(address);
            } else {
                registryConfig.setAddress(dubboInvocation.getAddress());
            }
            reference.setRegistry(registryConfig);

            String group = context.getMeta().getExtension().get("dubbo.group");
            // using special group
            if (StringUtils.isNotEmpty(group)) {
                reference.setGroup(group);
            } else {
                reference.setGroup(dubboInvocation.getGroup());
            }

            // set protocol / interface / version / timeout
            reference.setProtocol(dubboInvocation.getProtocol());
            reference.setInterface(dubboInvocation.getInterfaceName());
            if (StringUtils.isNotEmpty(dubboInvocation.getVersion())) {
                reference.setVersion(dubboInvocation.getVersion());
            }
            // timeout
            reference.setTimeout(context.getMeta().getTimeout());
            // use generic invoke
            reference.setGeneric(true);
            // fix issue #45
            reference.setRetries(0);

            RpcContext rpcContext = RpcContext.getContext();
            rpcContext.setAttachment(Constants.HEADER_TRACE_ID_X, context.getMeta().getTraceId());

            log.info("[ dubbo repeater ] {} ", JSON.toJSONString(reference));
            GenericService genericService = reference.get();
            return genericService.$invoke(dubboInvocation.getMethodName(), dubboInvocation.getParameterTypes(), invocation.getRequest());
        }catch (Exception e){
            log.error(" [ dubbo repeater ] fail " + JSON.toJSONString(dubboInvocation), e);
            throw e;
        } finally{
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    @Override
    public InvokeType getType() {
        return InvokeType.DUBBO;
    }

    @Override
    public String identity() {
        return "dubbo";
    }
}
