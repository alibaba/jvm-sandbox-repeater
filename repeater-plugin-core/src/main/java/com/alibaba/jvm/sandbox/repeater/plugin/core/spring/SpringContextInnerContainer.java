package com.alibaba.jvm.sandbox.repeater.plugin.core.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * <p>
 * SpringContext内部容器，如果应用是通过agent方式挂载sandbox，会保存bean信息到内部容器
 *
 * @author zhaoyb1990
 */
public class SpringContextInnerContainer {

    private SpringContextInnerContainer() {}

    private AtomicBoolean agentLaunch = new AtomicBoolean(false);

    private final static SpringContextInnerContainer INSTANCE = new SpringContextInnerContainer();

    /**
     * spring bean用map存放
     */
    private Map<String, Object> realBeanContext = new ConcurrentHashMap<String, Object>(16);
    private Map<String, Object> classNameContext = new ConcurrentHashMap<String, Object>(16);

    private static SpringContextInnerContainer getInstance() {
        return INSTANCE;
    }

    /**
     * 放入bean实例
     *
     * @param beanName spring原始beanName
     * @param bean     实例化的bean
     */
    public static void addBean(String beanName, Object bean) {
        getInstance().realBeanContext.put(beanName, bean);
        getInstance().classNameContext.put(bean.getClass().getName(), bean);
    }

    /**
     * 根据beanName获取bean
     *
     * @param beanName bean名称
     * @return bean的实例
     */
    static Object getBeanByName(String beanName) {
        return getInstance().realBeanContext.get(beanName);
    }

    /**
     * 根据beanType获取bean
     *
     * @param className bean类名或者接口名
     * @return bean的实例
     */
    static Object getBeanByType(String className) {
        return getInstance().classNameContext.get(className);
    }

    /**
     * 是否使用agent启动 - 如果是agent启动的，可以通过内部的container获取bean
     *
     * @return 是否agent挂载
     */
    static boolean isAgentLaunch() {
        return getInstance().agentLaunch.get();
    }

    /**
     * 设置启动模式
     *
     * @param launch 是否agent挂载
     */
    public static void setAgentLaunch(boolean launch) {
        getInstance().agentLaunch.set(launch);
    }
}
