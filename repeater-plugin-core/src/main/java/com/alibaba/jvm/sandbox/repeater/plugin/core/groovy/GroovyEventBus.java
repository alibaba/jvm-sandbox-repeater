package com.alibaba.jvm.sandbox.repeater.plugin.core.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 17:59
 */
public class GroovyEventBus implements GroovyPublisher {

    private List<GroovySubscriber> subscriberList = new LinkedList<>();

    private static final GroovyPublisher INSTANCE;

    static {
        INSTANCE = new GroovyEventBus();
        INSTANCE.add(new MockStrategyGroovySubscriber());
        INSTANCE.add(new InvocationHandlerGroovySubscriber());
    }

    public static GroovyPublisher instance() {
        return INSTANCE;
    }

    @Override
    public void add(GroovySubscriber subscriber) {
        subscriberList.add(subscriber);
    }

    @Override
    public void publish(GroovyConfig groovyObject) {
        for (GroovySubscriber subscriber :  subscriberList) {
            if (subscriber.getType().equals(groovyObject.getType())) {
                subscriber.subscribe(groovyObject);
            }
        }
    }

    @Override
    public void remove(GroovySubscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
