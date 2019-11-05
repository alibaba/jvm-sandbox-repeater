package com.alibaba.jvm.sandbox.repeater.module.classloader;

import com.alibaba.jvm.sandbox.api.annotation.Stealth;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

/**
 * 插件类加载器；父类加载器是sandbox's module classLoader
 * <p>
 *
 * @author zhaoyb1990
 */
@Stealth
public class PluginClassLoader extends URLClassLoader {

    private final static Logger log = LoggerFactory.getLogger(PluginClassLoader.class);

    private final List<Routing> routingArray = Lists.newArrayList();

    public PluginClassLoader(URL[] urls, ClassLoader parent, Routing... routingArray) {
        super(urls, parent);
        if (ArrayUtils.isNotEmpty(routingArray)) {
            this.routingArray.addAll(Arrays.asList(routingArray));
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        // plugin-api的类;slf4j;logback；使用模块加载
        if (userParent(name)) {
            try {
                return super.loadClass(name, resolve);
            } catch (Exception e) {
                // ignore
            }
        }

        // 特殊路由表的；先走特殊路由表；
        if (CollectionUtils.isNotEmpty(routingArray)) {
            for (final Routing routing : routingArray) {
                if (!routing.isHit(name)) {
                    continue;
                }
                final ClassLoader routingClassLoader = routing.classLoader;
                try {
                    return routingClassLoader.loadClass(name);
                } catch (Exception e) {
                    // ignore...
                }
            }
        }

        // 先查一次已加载类的缓存
        final Class<?> loadedClass = findLoadedClass(name);

        if (loadedClass != null) {
            return loadedClass;
        }

        try {
            final Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            return super.loadClass(name, resolve);
        }
    }

    /**
     * 是否使用父类加载（理论上PluginClassLoader除了特殊路由表之外的都可以用moduleClassloader的类）
     * <p>
     * 但为了插件能够更自由的引包，也破坏了双亲委派机制
     *
     * @param name 类名
     * @return 是否使用父类加载
     */
    private boolean userParent(String name) {
        for (String pattern : Constants.PLUGIN_CLASS_PATTERN) {
            if (name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 参考自<a>https://github.com/alibaba/jvm-sandbox/</a>
     * <p>
     * 尽可能关闭ClassLoader
     * <p>
     * URLClassLoader会打开指定的URL资源，在SANDBOX中则是对应的Jar文件，如果不在shutdown的时候关闭ClassLoader，会导致下次再次加载
     * 的时候，依然会访问到上次所打开的文件（底层被缓存起来了）
     * <p>
     * 在JDK1.7版本中，URLClassLoader提供了{@code close()}方法来完成这件事；但在JDK1.6版本就要下点手段了；
     * <p>
     * 该方法将会被{@code ControlModule#shutdown}通过反射调用，
     * 请保持方法声明一致
     */
    public void closeIfPossible() {

        // 如果是JDK7+的版本, URLClassLoader实现了Closeable接口，直接调用即可
        if (this instanceof Closeable) {
            try {
                final Method closeMethod = URLClassLoader.class.getMethod("close");
                closeMethod.invoke(this);
            } catch (Throwable cause) {
                // ignore...
            }
            return;
        }

        // 对于JDK6的版本，URLClassLoader要关闭起来就显得有点麻烦，这里弄了一大段代码来稍微处理下
        // 而且还不能保证一定释放干净了，至少释放JAR文件句柄是没有什么问题了
        try {
            final Object sun_misc_URLClassPath = URLClassLoader.class.getDeclaredField("ucp").get(this);
            final Object java_util_Collection = sun_misc_URLClassPath.getClass().getDeclaredField("loaders").get(
                    sun_misc_URLClassPath);

            for (Object sun_misc_URLClassPath_JarLoader : ((Collection) java_util_Collection).toArray()) {
                try {
                    final JarFile java_util_jar_JarFile = (JarFile) sun_misc_URLClassPath_JarLoader.getClass().getDeclaredField("jar").get(sun_misc_URLClassPath_JarLoader);
                    java_util_jar_JarFile.close();
                } catch (Throwable t) {
                    // if we got this far, this is probably not a JAR loader so skip it
                }
            }
        } catch (Throwable cause) {
            // ignore...
        }
    }

    /**
     * 参考自<a>https://github.com/alibaba/jvm-sandbox/</a>
     */
    public static class Routing {

        private final Collection<String> regexExpresses = new ArrayList<String>();

        private final ClassLoader classLoader;

        /**
         * 构造类加载路由匹配器
         *
         * @param classLoader       目标ClassLoader
         * @param regexExpressArray 匹配规则表达式数组
         */
        public Routing(final ClassLoader classLoader, final String... regexExpressArray) {
            if (ArrayUtils.isNotEmpty(regexExpressArray)) {
                regexExpresses.addAll(Arrays.asList(regexExpressArray));
            }
            this.classLoader = classLoader;
        }

        /**
         * 当前参与匹配的Java类名是否命中路由匹配规则
         * 命中匹配规则的类加载,将会从此ClassLoader中完成对应的加载行为
         *
         * @param javaClassName 参与匹配的Java类名
         * @return true:命中;false:不命中;
         */
        private boolean isHit(final String javaClassName) {
            for (final String regexExpress : regexExpresses) {
                try {
                    if (javaClassName.matches(regexExpress)) {
                        return true;
                    }
                } catch (Throwable cause) {
                    log.warn("routing {} failed, regex-express = {}", javaClassName, regexExpress, cause);
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "Routing{" +
                    "regexExpresses=" + regexExpresses +
                    ", classLoader=" + classLoader +
                    '}';
        }
    }
}
