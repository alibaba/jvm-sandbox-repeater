package com.alibaba.jvm.sandbox.repeater.aide.compare;

import java.util.Collection;
import java.util.Map;

/**
 * {@link TypeUtils}
 * <p>
 *
 * @author zhaoyb1990
 */
public class TypeUtils {

    public static boolean isBothJavaLang(Class<?> lCs, Class<?> rCs) {
        return isJavaLang(lCs) && isJavaLang(rCs);
    }

    public static boolean isBothJavaMath(Class<?> lCs, Class<?> rCs) {
        return isJavaMath(lCs) && isJavaMath(rCs);
    }

    public static boolean isBothJavaTime(Class<?> lCs, Class<?> rCs) {
        return isJavaTime(lCs) && isJavaTime(rCs);
    }

    public static boolean isBothJavaUtil(Class<?> lCs, Class<?> rCs) {
        return isJavaUtil(lCs) && isJavaUtil(rCs);
    }

    public static boolean isSamePackagePrefix(Class<?> clazz, String packagePrefix) {
        return clazz.getCanonicalName().startsWith(packagePrefix);
    }

    public static boolean isBasicType(Class<?> lCs, Class<?> rCs) {
        return lCs.isPrimitive() && rCs.isPrimitive();
    }

    public static boolean isJavaWellKnown(Class<?> clazz) {
        return isJavaLang(clazz) || isJavaTime(clazz) || isJavaMath(clazz) || isJavaUtil(clazz);
    }

    public static boolean isJavaLang(Class<?> clazz) {
        return isSamePackagePrefix(clazz, "java.lang");
    }

    public static boolean isJavaTime(Class<?> clazz) {
        return isSamePackagePrefix(clazz, "java.time");
    }

    public static boolean isJavaMath(Class<?> clazz) {
        return isSamePackagePrefix(clazz, "java.math");
    }

    public static boolean isJavaUtil(Class<?> clazz) {
        return isSamePackagePrefix(clazz, "java.util");
    }


    public static boolean isMap(Class<?> lCs, Class<?> rCs) {
        return Map.class.isAssignableFrom(lCs) && Map.class.isAssignableFrom(rCs);
    }

    public static boolean isArray(Class<?> lCs, Class<?> rCs) {
        return lCs.isArray() && rCs.isArray();
    }

    public static boolean isCollection(Class<?> lCs, Class<?> rCs) {
        return Collection.class.isAssignableFrom(lCs) && Collection.class.isAssignableFrom(rCs);
    }
}
