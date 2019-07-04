package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;

/**
 * {@link PathUtils} 插件路径
 * <p>
 * repeater插件目录如下；如果使用默认路径，可以通过repeater-module.jar定位到 plugins & cfg 的位置
 * <p>
 *
 * ├── cfg
 * │   ├── repeater-logback.xml
 * │   └── repeater.properties
 * ├── plugins
 * │   ├── dubbo-plugin.jar
 * │   ├── http-plugin.jar
 * │   ├── ibatis-plugin.jar
 * │   ├── java-plugin.jar
 * │   └── mybatis-plugin.jar
 * ├── repeater-bootstrap.jar
 * └── repeater-module.jar
 * </p>
 *
 * @author zhaoyb1990
 */
public class PathUtils {

    /**
     * 获取插件绝对路径
     *
     * @return 插件绝对路径
     */
    public static String getPluginPath() {
        String modulePath = getModulePath();
        if (StringUtils.isEmpty(modulePath)) {
            return null;
        }
        return modulePath + "/plugins";
    }

    /**
     * 获取配置文件路径
     *
     * @return 配置绝对路径
     */
    public static String getConfigPath() {
        String modulePath = getModulePath();
        if (StringUtils.isEmpty(modulePath)) {
            return null;
        }
        return modulePath + "/cfg";
    }


    /**
     * 获取当前模块绝对路径
     *
     * @return 模块绝对路径
     */
    public static String getModulePath() {
        // moduleClassloader
        ClassLoader classLoader = PathUtils.class.getClassLoader();
        // moduleClassloader class
        Class<? extends ClassLoader> aClass = classLoader.getClass();
        try {
            Field field = aClass.getDeclaredField("moduleJarFile");
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            File file = (File) field.get(classLoader);
            field.setAccessible(accessible);
            return file.getParentFile().getAbsolutePath();
        } catch (Throwable e) {
            // ignore
        }
        return null;
    }
}
