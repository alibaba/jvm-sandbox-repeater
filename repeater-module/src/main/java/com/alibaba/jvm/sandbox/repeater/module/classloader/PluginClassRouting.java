package com.alibaba.jvm.sandbox.repeater.module.classloader;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * {@link PluginClassRouting}
 * <p>
 * 插件自定义类路由;
 * <p>
 * 使用场景：http录制/dubbo回放，通过无侵入方式无法有效隔离，插件要利用业务APPClassloader的类进行直接调用，不想通过很多反射来写，
 * 甚至像dubbo的回放，需要依赖的东西太多，打包到插件里面会增加包大小，同时会重复加载dubbo框架的类，增加很多的metaspace开销
 * <p>
 * 解决办法：由于类路由需要在{@link PluginClassLoader}初始化之前获取，无法用SPI的方式给各个插件去实现，因此只能通过配置实现，目前没有想到特别优雅的解法
 * 1. 通过定义规范的配置文件，放在插件jar包的固定位置，module通过读文件的方式进行解析。 多了一次文件IO操作，不会产生像SPI那样大的开销
 * <p>
 * 2. 通过Hard code编码在module中，缺点是插件的使用需要跟module耦合起来
 * <p>
 * 暂时先采用方案2；后续再优化
 *
 * @author zhaoyb1990
 */
public class PluginClassRouting {

    /**
     * 目标类
     */
    private String targetClass;

    /**
     * 路由的目标类正则表达式
     */
    private String classPattern;

    /**
     * 当前路由标志（{@link Repeater#identity()}（{@link InvokePlugin#identity()}
     */
    private String identity;

    /**
     * 匹配当前规则是否生效
     */
    private Matcher matcher;

    /**
     * 当前路由若未找到合适classloader是否阻断启动
     */
    private boolean block;

    /**
     * 临时的实现方式，把一些插件、回放器的路由规则写在这里面；
     *
     * @param isPreloading 是否预加载
     * @param timeout      超时时间
     * @return 路由表
     */
    public static PluginClassLoader.Routing[] wellKnownRouting(boolean isPreloading, Long timeout) {
        // http插件对servlet-api路由
        PluginClassRouting httpPluginRouting = PluginClassRouting.builder()
                .targetClass("javax.servlet.http.HttpServlet")
                .classPattern("^javax.servlet..*")
                .identity("http")
                .matcher(Matcher.PLUGIN)
                .block(true)
                .build();
        // dubbo回放器中对dubbo框架路由
        PluginClassRouting dubboRepeaterRouting = PluginClassRouting.builder()
                .targetClass("org.apache.dubbo.rpc.model.ApplicationModel")
                .classPattern("^org.apache.dubbo..*")
                .identity("dubbo")
                .matcher(Matcher.REPEATER)
                .block(false)
                .build();
        return transformRouting(Lists.newArrayList(httpPluginRouting, dubboRepeaterRouting), isPreloading, timeout);
    }

    /**
     * 转换路由规则
     *
     * @param routingList  路由规则列表
     * @param isPreloading 是否预加载（agent启动的时候部分类还未加载）
     * @param timeout      超时时间(s)
     * @return 特殊路由列表
     */
    private static PluginClassLoader.Routing[] transformRouting(List<PluginClassRouting> routingList,
                                                                boolean isPreloading,
                                                                Long timeout) {
        List<PluginClassLoader.Routing> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(routingList)) {
            for (PluginClassRouting routing : routingList) {
                PluginClassLoader.Routing r = transformRouting(routing, isPreloading, timeout);
                if (r != null) {
                    LogUtil.info("using target class loader to load plugin,class={}", r);
                    result.add(r);
                } else {
                    LogUtil.warn("no valid classloader found in routing,routing={}", routing);
                }
            }
        }
        return result.toArray(new PluginClassLoader.Routing[0]);
    }

    /**
     * 转换路由规则
     *
     * @param routing      路由规则
     * @param isPreloading 是否预加载（agent启动的时候部分类还未加载）
     * @param timeout      超时时间(s)
     * @return 特殊路由列表
     */
    private static PluginClassLoader.Routing transformRouting(PluginClassRouting routing, boolean isPreloading, Long timeout) {
        PluginClassLoader.Routing target = null;
        // 100ms
        timeout = timeout * 10;
        if (routing.match()) {
            while (isPreloading && --timeout > 0 && ClassloaderBridge.instance().findClassInstances(routing.targetClass).size() == 0) {
                try {
                    Thread.sleep(100);
                    LogUtil.info("{} required {} class router,waiting for class loading", routing.identity, routing.targetClass);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        List<Class<?>> instances = ClassloaderBridge.instance().findClassInstances(routing.targetClass);
        // ensure only one classloader will be found
        if (instances.size() > 1 && routing.block) {
            throw new RuntimeException("found multiple" + routing.targetClass + "loaded in container, can't use start repeater with special routing.");
        } else if (instances.size() == 1) {
            Class<?> aClass = instances.get(0);
            target = new PluginClassLoader.Routing(aClass.getClassLoader(), routing.classPattern);
        } else {
            routing.notFoundAction();
        }
        return target;
    }

    private static Builder builder() {
        return new Builder();
    }

    private boolean match() {
        return matcher != null && matcher.match(identity);
    }

    private void notFoundAction() {
        if (matcher != null) {
            matcher.notFoundAction(identity);
        }
    }

    @Override
    public String toString() {
        return "PluginClassRouting{" +
                "targetClass='" + targetClass + '\'' +
                ", classPattern='" + classPattern + '\'' +
                ", identity='" + identity + '\'' +
                ", matcher=" + matcher +
                ", block=" + block +
                '}';
    }

    /**
     *
     */
    enum Matcher {

        /**
         * 插件matcher
         */
        PLUGIN("plugin") {
            @Override
            public boolean match(String identity) {
                return ApplicationModel.instance().getConfig().getPluginIdentities().contains(identity);
            }

            @Override
            public void notFoundAction(String identity) {
                ApplicationModel.instance().getConfig().getPluginIdentities().remove(identity);
            }
        },

        /**
         * 回放器matcher
         */
        REPEATER("repeater") {
            @Override
            public boolean match(String identity) {
                return ApplicationModel.instance().getConfig().getRepeatIdentities().contains(identity);
            }

            @Override
            public void notFoundAction(String identity) {
                ApplicationModel.instance().getConfig().getRepeatIdentities().remove(identity);
            }
        },
        ;

        private String name;

        Matcher(String name) {
            this.name = name;
        }

        public abstract boolean match(String identity);

        public abstract void notFoundAction(String identity);

        public String getName() {
            return name;
        }
    }

    public static final class Builder {
        private String targetClass;
        private String classPattern;
        private String identity;
        private Matcher matcher;
        private boolean block;

        private Builder() {
        }

        public Builder targetClass(String targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public Builder classPattern(String classPattern) {
            this.classPattern = classPattern;
            return this;
        }

        public Builder identity(String identity) {
            this.identity = identity;
            return this;
        }

        public Builder matcher(Matcher matcher) {
            this.matcher = matcher;
            return this;
        }

        public Builder block(boolean block) {
            this.block = block;
            return this;
        }

        public PluginClassRouting build() {
            PluginClassRouting pluginClassRouting = new PluginClassRouting();
            pluginClassRouting.matcher = this.matcher;
            pluginClassRouting.classPattern = this.classPattern;
            pluginClassRouting.identity = this.identity;
            pluginClassRouting.targetClass = this.targetClass;
            pluginClassRouting.block = this.block;
            return pluginClassRouting;
        }
    }
}
