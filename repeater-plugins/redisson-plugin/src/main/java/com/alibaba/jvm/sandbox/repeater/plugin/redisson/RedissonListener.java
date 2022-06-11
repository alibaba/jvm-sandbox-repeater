package com.alibaba.jvm.sandbox.repeater.plugin.redisson;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;

/**
 * {@link RedissonListener} redis客户端redisson子插件
 *
 * @author quansong
 * @version 1.0
 */
public class RedissonListener extends DefaultEventListener {

    private static final String REDISSON_CLAAS_PATH = "org.redisson.Redisson";

    RedissonListener(InvokeType invokeType,
                     boolean entrance,
                     InvocationListener listener,
                     InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    /**
     * 处理before事件
     * 通配符过滤Redisson类
     *
     * @param event before事件
     */
    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        if(REDISSON_CLAAS_PATH.equalsIgnoreCase(event.javaClassName)){
            return;
        }
        super.doBefore(event);
    }


}
