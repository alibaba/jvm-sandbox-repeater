package com.alibaba.jvm.sandbox.repeater.plugin.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * ParameterTypesUtil - 参数类型
 *
 * @author vivo-刘延江
 * @version 1.0
 * @CreateDate: 2020/10/29 17:42
 */
public class ParameterTypesUtil {

    public static String getTypesStrByClasses(Class<?>[] parameterClasses){
        String[]parameterTypes = getTypesArrayByClasses(parameterClasses);
        if(parameterClasses == null){
            return null;
        }
        return "("+Joiner.on(',').join(parameterTypes)+")";
    }

    public static String getTypesStrByObjects(Object[] parameters){
        if(parameters ==null){
            return null;
        }
        String[]parameterTypes = getTypesArrayByObjects(parameters);
        if(parameterTypes == null){
            return null;
        }
        return "("+Joiner.on(',').skipNulls().join(parameterTypes)+")";
    }

    private static String[] getTypesArrayByObjects(Object[] objects){
        if(objects ==null || objects.length == 0){
            return null;
        }
        List<String>parameterTypeList= Lists.newArrayListWithExpectedSize(6);
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                String type = objects[i].getClass().getCanonicalName();
                if (type != null) {
                    parameterTypeList.add(type);
                }
            }
        }
        return parameterTypeList.toArray(new String[parameterTypeList.size()]);
    }
    public static Class[] getTypeClassArrayByObjects(Object[] objects){
        if(objects ==null || objects.length == 0){
            return null;
        }
        Class[]parameterTypes = new Class[objects.length];
        for(int i=0;i<objects.length;i++){
            if (objects[i] != null) {
                parameterTypes[i] = objects[i].getClass();
            }
            else {
                parameterTypes[i] = null;
            }
        }
        return parameterTypes;
    }

    private static String[] getTypesArrayByClasses(Class<?>[] parameterClasses){
        if(parameterClasses ==null){
            return null;
        }
        String[]parameterTypes = new String[parameterClasses.length];
        for(int i=0;i<parameterClasses.length;i++){
            parameterTypes[i] = parameterClasses[i].getCanonicalName();
        }
        return parameterTypes;
    }
}
