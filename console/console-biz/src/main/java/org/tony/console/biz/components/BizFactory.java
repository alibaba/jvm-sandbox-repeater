package org.tony.console.biz.components;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.tony.console.common.exception.BizException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author peng.hu1
 * @Date 2022/12/16 15:25
 */
@Component
public class BizFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ConcurrentHashMap serviceMap = new ConcurrentHashMap();

    public <T> List<T> getServiceWithOrder(Class<T> tClass) {
        Map<String, T> beanMap =  applicationContext.getBeansOfType(tClass);
        Collection<T> collection = beanMap.values();

        Map<Integer, T> orderServiceMap = new HashMap<>();
        for (T t : collection) {
            Order order = t.getClass().getAnnotation(Order.class);
            if (order!=null) {
                orderServiceMap.put(order.value(), t);
            } else {
                orderServiceMap.put(0, t);
            }
        }

        Integer[] kArray = new Integer[orderServiceMap.size()];
        orderServiceMap.keySet().toArray(kArray);
        Arrays.sort(kArray);

        List<T> tList = new LinkedList<>();
        for (Integer k : kArray) {
            tList.add(orderServiceMap.get(k));
        }

        return tList;
    }

    /**
     * 执行
     * @param service 服务
     * @param request 请求
     * @param <Request>
     * @param <Service>
     * @throws BizException
     */
    public <Request, Service extends BizComService> void execute(Class<Service> service, Request request) throws BizException {
        List<Service> serviceList;
        if (serviceMap.containsKey(service)) {
            serviceList = (List<Service>) serviceMap.get(service);
        } else {
            serviceList = getServiceWithOrder(service);
            serviceMap.put(service, serviceList);
        }

        for (Service s: serviceList) {
            if (s.isSupport(request)) {
                s.execute(request);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
