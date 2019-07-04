package com.alibaba.repeater.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;

/**
 * {@link SpringContextContainer} 应用改造之后的springBean容器；给sandbox模块提供了{@code getInstance}的钩子
 * <p>
 *
 * @author zhaoyb1990
 */
public class SpringContextContainer {

    private SpringContextContainer() {}

    private static SpringContextContainer INSTANCE = new SpringContextContainer();

    /**
     * spring bean用map存放
     */
    private Map<String, Object> realBeanContext = new ConcurrentHashMap<String, Object>(16);
    private Map<String, Object> classNameContext = new ConcurrentHashMap<String, Object>(16);

    private ApplicationContext context;

    public ApplicationContext getContext() {
        return context;
    }

    void setContext(ApplicationContext context) {
        this.context = context;
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        if (beanDefinitionNames != null && beanDefinitionNames.length > 0) {
            for (String beanName : context.getBeanDefinitionNames()) {
                try {
                    addBean(beanName, context.getBean(beanName));
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
    }

    public static SpringContextContainer getInstance() {
        return INSTANCE;
    }

    /**
     * 放入bean实例
     *
     * @param beanName spring原始beanName
     * @param bean     实例化的bean
     */
    private static void addBean(String beanName, Object bean) {
        getInstance().realBeanContext.put(beanName, bean);
        getInstance().classNameContext.put(bean.getClass().getName(), bean);
    }

    /**
     * 根据beanName获取bean
     *
     * @param beanName bean名称
     * @return bean的实例
     */
    public static Object getBeanByName(String beanName) {
        return getInstance().realBeanContext.get(beanName);
    }

    /**
     * 根据beanType获取bean
     *
     * @param className bean类名或者接口名
     * @return bean的实例
     */
    public static Object getBeanByType(String className) {
        return getInstance().classNameContext.get(className);
    }
}
