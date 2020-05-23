package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

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
import org.apache.dubbo.rpc.service.GenericService;
import org.kohsuke.MetaInfServices;

import java.util.Optional;


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
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("jvm-sandbox-repeater");
        // require address to initialize registry config
        RegistryConfig registryConfig = new RegistryConfig();
        String address = context.getMeta().getExtension().get("dubbo.address");
        // using special address
        if (StringUtils.isNotEmpty(address)) {
            registryConfig.setAddress(address);
        } else {
            registryConfig.setAddress(dubboInvocation.getAddress());
        }
        String group = context.getMeta().getExtension().get("dubbo.group");
        // using special group
        if (StringUtils.isNotEmpty(group)) {
            registryConfig.setGroup(group);
        } else {
            registryConfig.setGroup(dubboInvocation.getGroup());
        }
        reference.setApplication(ConfigManager.getInstance().getApplication().orElse(applicationConfig));
        reference.setRegistry(registryConfig);

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
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(GenericService.class.getClassLoader());
            GenericService genericService = reference.get();
            return genericService.$invoke(dubboInvocation.getMethodName(), dubboInvocation.getParameterTypes(), invocation.getRequest());
        } finally {
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
