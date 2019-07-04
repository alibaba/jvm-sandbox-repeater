package com.alibaba.jvm.sandbox.repeater.module;

import com.alibaba.jvm.sandbox.api.spi.ModuleJarUnLoadSpi;
import com.alibaba.jvm.sandbox.repeater.module.util.LogbackUtils;
import org.kohsuke.MetaInfServices;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(ModuleJarUnLoadSpi.class)
public class ModuleJarUnLoadCompleted implements ModuleJarUnLoadSpi {

    @Override
    public void onJarUnLoadCompleted() {
        try {
            LogbackUtils.destroy();
        } catch (Throwable e) {
            // ignore
        }
    }
}
