package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvocationHandler;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng.hu1
 * @Date 2023/4/23 15:51
 */
public class InvocationHandlerGroovySubscriber implements GroovySubscriber {

    protected final static Logger log = LoggerFactory.getLogger(InvocationHandlerGroovySubscriber.class);

    GroovyClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader());

    @Override
    public void subscribe(GroovyConfig groovyConfig) {
        if (groovyConfig.getValid() && GroovyCache.contains(groovyConfig)) {
            return;
        }

        //版本没有更新，我们认为没有更新
        if (!groovyConfig.getValid() && GroovyCache.contains(groovyConfig)){
            GroovyCache.removeInvocationHandler(groovyConfig.getId());
            log.info("success remove groovy script id={} version={}", groovyConfig.getId(),  groovyConfig.getVersion());
            return;
        }

        if (groovyConfig.getValid() && !GroovyCache.contains(groovyConfig)) {
            try {
                Class groovyCls = classLoader.parseClass(groovyConfig.getContent());
                Object groovyObj = groovyCls.newInstance();
                if (groovyObj instanceof InvocationHandler) {
                    GroovyCache.addInvocationHandler(groovyConfig.getId(), (InvocationHandler)groovyObj, groovyConfig.getVersion());
                    log.info("success update InvocationHandler script id={} version={}", groovyConfig.getId(),  groovyConfig.getVersion());
                    log.debug("script id={} content={}", groovyConfig.getId(), groovyConfig.getContent());
                } else {
                    log.error("appName={} id={} groovy script is not instance of InvocationHandler", groovyConfig.getAppName(), groovyConfig.getId());
                }

            } catch (Exception e) {
                log.error("appName={} id={} groovy parse error", groovyConfig.getAppName(), groovyConfig.getId(), e);
            }
        }
    }

    @Override
    public GroovyType getType() {
        return GroovyType.InvocationHandler;
    }
}
