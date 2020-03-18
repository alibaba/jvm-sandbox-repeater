package com.alibaba.jvm.sandbox.repeater.plugin.core.spring;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.apache.commons.lang3.reflect.MethodUtils.invokeStaticMethod;

/**
 * {@link SpringContextAdapter} SpringBean获取适配
 * <p>
 *
 * @author zhaoyb1990
 */
public class SpringContextAdapter {

    private final static Logger log = LoggerFactory.getLogger(SpringContextAdapter.class);

    private final static String SPRING_CONTAINER_CLASS = "com.alibaba.repeater.client.SpringContextContainer";

    private volatile static Class<?> springContainerClass;

    /**
     * 根据beanName获取bean
     *
     * @param beanName bean名称
     * @return bean的实例
     */
    public static Object getBeanByName(String beanName) {
        return SpringContextInnerContainer.isAgentLaunch()
            ? SpringContextInnerContainer.getBeanByName(beanName)
            : getBeanByNameFromContainer(beanName);
    }

    /**
     * 根据beanType获取bean
     *
     * @param className bean类名或者接口名
     * @return bean的实例
     */
    public static Object getBeanByType(String className) {
        return SpringContextInnerContainer.isAgentLaunch()
            ? SpringContextInnerContainer.getBeanByType(className)
            : getBeanByTypeFromContainer(className);
    }

    /**
     * 从容器中根据beanName获取bean
     *
     * @param beanName bean名称
     * @return bean的实例
     */
    private static Object getBeanByNameFromContainer(String beanName) {
        Class<?> classInstance = getSpringContainerClass();
        if (classInstance == null) {
            return null;
        }
        try {
            return invokeStaticMethod(classInstance, "getBeanByName", beanName);
        } catch (Exception e) {
            log.error("[InvokeError]-reflection invoke error", e);
            ApplicationModel.instance().exceptionOverflow(e);
            return null;
        }

    }

    /**
     * 从容器中根据beanType获取bean
     *
     * @param className bean类名或者接口名
     * @return bean的实例
     */
    private static Object getBeanByTypeFromContainer(String className) {
        Class<?> classInstance = getSpringContainerClass();
        if (classInstance == null) {
            return null;
        }
        try {
            return invokeStaticMethod(classInstance, "getBeanByType", className);
        } catch (Exception e) {
            log.error("[InvokeError]-reflection invoke error", e);
            ApplicationModel.instance().exceptionOverflow(e);
            return null;
        }
    }

    /**
     * 获取springContainerClass的类
     *
     * @return SpringContextContainer类
     */
    private static Class<?> getSpringContainerClass() {
        if (springContainerClass == null) {
            synchronized (SpringContextAdapter.class) {
                if (springContainerClass == null) {
                    springContainerClass = ClassloaderBridge.instance().findClassInstance(SPRING_CONTAINER_CLASS);
                }
            }
        }
        return springContainerClass;
    }
}
