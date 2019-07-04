package com.alibaba.jvm.sandbox.repeater.module.util;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SPILoader} 加载spi
 * <p>
 *
 * @author zhaoyb1990
 */
public class SPILoader {

    private final static Logger log = LoggerFactory.getLogger(SPILoader.class);

    public static <T> List<T> loadSPI(Class<T> spiType, ClassLoader classLoader) {
        ServiceLoader<T> loaded = ServiceLoader.load(spiType, classLoader);
        Iterator<T> spiIterator = loaded.iterator();
        List<T> target = Lists.newArrayList();
        while (spiIterator.hasNext()) {
            try {
                target.add(spiIterator.next());
            } catch (Throwable e) {
                log.error("Error load spi {} >>> ", spiType.getCanonicalName(), e);
            }
        }
        return target;
    }
}
