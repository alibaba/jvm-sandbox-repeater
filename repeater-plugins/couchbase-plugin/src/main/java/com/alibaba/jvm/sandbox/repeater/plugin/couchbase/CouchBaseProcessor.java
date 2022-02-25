package com.alibaba.jvm.sandbox.repeater.plugin.couchbase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 * @author wangyeran
 */
class CouchBaseProcessor extends DefaultInvocationProcessor {

    CouchBaseProcessor(InvokeType type) {
        super(type);
    }

    protected static Logger log = LoggerFactory.getLogger(CouchBaseProcessor.class);

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        if (event.argumentArray[0].getClass().toString().contains("ArrayList$Itr")) {
            Class cz = event.argumentArray[0].getClass();
            try {
                Method method = cz.getDeclaredMethod("hasNext");
                Method method1 = cz.getDeclaredMethod("next");
                method.setAccessible(true);
                method1.setAccessible(true);
                List<Object> list = new ArrayList<Object>();
                while (Boolean.valueOf(method.invoke(event.argumentArray[0]).toString())) {
                    list.add(method1.invoke(event.argumentArray[0]));
                }
                Class<?> arrayListClass = event.javaClassLoader.loadClass("java.util.ArrayList");
                Object arrayList = arrayListClass.newInstance();
                for (Object o : list) {
                    MethodUtils.invokeMethod(arrayList, "add", o);
                }
                Method methodIterator = arrayListClass.getDeclaredMethod("iterator");
                methodIterator.setAccessible(true);
                Object iterator = methodIterator.invoke(arrayList);
                event.argumentArray[0] = iterator;
                return new Identity(InvokeType.COUCH_BASE.name(), list.toString(), "Unknown", new HashMap<String, String>(1));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
                log.error("couchbase assembleIdentity error {}, object:{}, event arg:{}, func:{}",
                        e, event.target, event.argumentArray, event.javaClassName + event.javaMethodName); e.printStackTrace();
            }
        }
        return new Identity(InvokeType.COUCH_BASE.name(), event.argumentArray[0].toString(), "Unknown", new HashMap<String, String>(1));

    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (event.argumentArray[0].getClass().toString().contains("ArrayList$Itr")) {
            Class cz = event.argumentArray[0].getClass();
            try {
                Method method = cz.getDeclaredMethod("hasNext");
                Method method1 = cz.getDeclaredMethod("next");
                method.setAccessible(true);
                method1.setAccessible(true);
                List<Object> list = new ArrayList<Object>();
                while (Boolean.valueOf(method.invoke(event.argumentArray[0]).toString())) {
                    list.add(method1.invoke(event.argumentArray[0]));
                }
                Class<?> arrayListClass = event.javaClassLoader.loadClass("java.util.ArrayList");
                Object arrayList = arrayListClass.newInstance();
                for (Object o : list) {
                    MethodUtils.invokeMethod(arrayList, "add", o);
                }
                Method methodIterator = arrayListClass.getDeclaredMethod("iterator");
                methodIterator.setAccessible(true);
                Object iterator = methodIterator.invoke(arrayList);
                event.argumentArray[0] = iterator;
                return new Object[]{list};
            } catch (Exception e) {
                log.error("couchbase assembleRequest error {}, object:{}, event arg:{}, func:{}",
                        e, event.target, event.argumentArray, event.javaClassName + event.javaMethodName); e.printStackTrace();
            }
        }
        return new Object[]{event.argumentArray[0]};
    }
}
