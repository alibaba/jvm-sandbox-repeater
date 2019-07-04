package com.alibaba.jvm.sandbox.repeater.plugin.core.bridge;

import com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link ClassloaderBridge} 类加载桥接模式
 * <p>
 * repeater核心能力之一，classLoader还原，保证序列化/反序列化能够完整还原
 * <p>
 * 需要优化的点：init之后没有更新claCache；若运行时（agent方式启动时可能大部分cls无法缓存）新增cls，没有及时更新
 * </p>
 *
 * @author zhaoyb1990
 */
public class ClassloaderBridge {

    private final static String BOOTSTRAP_CLASSLOADER = "BootstrapClassLoader";
    private static ClassloaderBridge instance;
    private final Map<String, ClassLoader> clsCached = Maps.newConcurrentMap();
    private final LoadedClassDataSource loadedClassDataSource;

    private ClassloaderBridge(LoadedClassDataSource loadedClassDataSource) {
        this.loadedClassDataSource = loadedClassDataSource;
    }

    public static ClassloaderBridge instance() {
        return instance;
    }

    /**
     * 初始化内容；需要再模块加载的时候进行显示初始化
     *
     * @param loadedClassDataSource sandbox-api提供的已加载的类集合
     * @see com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource
     */
    public synchronized static void init(LoadedClassDataSource loadedClassDataSource) {
        instance = new ClassloaderBridge(loadedClassDataSource);
        instance().cacheClassLoader();
    }

    private void cacheClassLoader() {
        Iterator<Class<?>> iterator = loadedClassDataSource.iteratorForLoadedClasses();
        while (iterator.hasNext()) {
            final Class<?> next = iterator.next();
            ClassLoader loader = next.getClassLoader();
            if (loader != null) {
                clsCached.put(encode(loader), loader);
            }
        }
    }

    /**
     * 通过编码的token获取具体的classLoader
     *
     * @param token 编码后的token
     * @return classLoader 类加载器
     */
    public ClassLoader decode(String token) {
        if (StringUtils.equals(BOOTSTRAP_CLASSLOADER, token)) {
            return null;
        }
        ClassLoader loader = clsCached.get(token);
        // reset cache;会一定的性能开销
        if (loader == null) {
            cacheClassLoader();
        }
        return clsCached.get(token);
    }

    /**
     * 通过classLoader获得编码后token
     * <p>
     * 目前这个方式不够严谨；如果采用classLoader隔离（类似sandbox的classLoader）时不work，需要重写一下classLoader的toString
     * </p>
     *
     * @param classLoader 类加载器
     * @return 编码后的token
     */
    public String encode(ClassLoader classLoader) {
        if (classLoader == null) {
            return "BootstrapClassLoader";
        }
        return classLoader.getClass().getName();
    }

    /**
     * 根据className找合适的已加载类
     *
     * @param className 类全名
     * @return 具体加载的类
     */
    public Class<?> findClassInstance(String className) {
        Iterator<Class<?>> iterator = loadedClassDataSource.iteratorForLoadedClasses();
        while (iterator.hasNext()) {
            final Class<?> next = iterator.next();
            if (className.equals(next.getName()) && !isSandboxLoadedClass(next)) {
                return next;
            }
        }
        return null;
    }

    /**
     * 根据className找合适的已加载类集合（多个classLoader）
     *
     * @param className 类全名
     * @return 具体加载的类
     */
    public List<Class<?>> findClassInstances(String className) {
        List<Class<?>> classes = Lists.newArrayList();
        Iterator<Class<?>> iterator = loadedClassDataSource.iteratorForLoadedClasses();
        while (iterator.hasNext()) {
            final Class<?> next = iterator.next();
            if (className.equals(next.getName()) && !isSandboxLoadedClass(next)) {
                classes.add(next);
            }
        }
        return classes;
    }

    /**
     * 是否sandbox classloader加载的类
     *
     * @param clazz 目标类
     * @return true / false
     */
    private boolean isSandboxLoadedClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.getClassLoader().getClass().getName().contains("sandbox");
    }
}
