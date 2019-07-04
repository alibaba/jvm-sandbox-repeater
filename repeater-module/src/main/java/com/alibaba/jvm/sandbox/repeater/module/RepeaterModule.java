package com.alibaba.jvm.sandbox.repeater.module;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Information.Mode;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.resource.ConfigInfo;
import com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource;
import com.alibaba.jvm.sandbox.api.resource.ModuleController;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.module.advice.SpringInstantiateAdvice;
import com.alibaba.jvm.sandbox.repeater.module.classloader.PluginClassLoader;
import com.alibaba.jvm.sandbox.repeater.module.impl.JarFileLifeCycleManager;
import com.alibaba.jvm.sandbox.repeater.module.util.LogbackUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.api.ConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.StandaloneSwitch;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PathUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.LifecycleManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.RepeaterBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.eventbus.EventBusInner;
import com.alibaba.jvm.sandbox.repeater.plugin.core.eventbus.RepeatEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextInnerContainer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TtlConcurrentAdvice;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.ExecutorInner;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.SubscribeSupporter;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Module.class)
@Information(id = com.alibaba.jvm.sandbox.repeater.module.Constants.MODULE_ID, author = "zhaoyb1990", version = com.alibaba.jvm.sandbox.repeater.module.Constants.VERSION)
public class RepeaterModule implements Module, ModuleLifecycle {

    private final static Logger log = LoggerFactory.getLogger(RepeaterModule.class);

    @Resource
    private ModuleEventWatcher eventWatcher;

    @Resource
    private ModuleController moduleController;

    @Resource
    private ConfigInfo configInfo;

    @Resource
    private LoadedClassDataSource loadedClassDataSource;

    private Broadcaster broadcaster;

    private InvocationListener invocationListener;

    private ConfigManager configManager;

    private LifecycleManager lifecycleManager;

    private List<InvokePlugin> invokePlugins;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void onLoad() throws Throwable {
        // 初始化日志框架
        LogbackUtils.init(PathUtils.getConfigPath() + "/repeater-logback.xml");
        Mode mode = configInfo.getMode();
        log.info("module on loaded,id={},version={},mode={}", com.alibaba.jvm.sandbox.repeater.module.Constants.MODULE_ID, com.alibaba.jvm.sandbox.repeater.module.Constants.VERSION, mode);
        /* agent方式启动 */
        if (mode == Mode.AGENT) {
            log.info("agent launch mode,use Spring Instantiate Advice to register bean.");
            SpringContextInnerContainer.setAgentLaunch(true);
            SpringInstantiateAdvice.watcher(this.eventWatcher).watch();
            moduleController.active();
        }
    }

    @Override
    public void onUnload() throws Throwable {
        if (lifecycleManager != null) {
            lifecycleManager.release();
        }
    }

    @Override
    public void onActive() throws Throwable {
        log.info("onActive");
    }

    @Override
    public void onFrozen() throws Throwable {
        log.info("onFrozen");
    }

    @Override
    public void loadCompleted() {
        ExecutorInner.execute(new Runnable() {
            @Override
            public void run() {
                configManager = StandaloneSwitch.instance().getConfigManager();
                broadcaster = StandaloneSwitch.instance().getBroadcaster();
                invocationListener = new DefaultInvocationListener(broadcaster);
                RepeaterResult<RepeaterConfig> pr = configManager.pullConfig();
                if (pr.isSuccess()) {
                    log.info("pull repeater config success,config={}", pr.getData());
                    ClassloaderBridge.init(loadedClassDataSource);
                    initialize(pr.getData());
                }
            }
        });
    }

    /**
     * 初始化插件
     *
     * @param config 配置文件
     */
    private synchronized void initialize(RepeaterConfig config) {
        if (initialized.compareAndSet(false, true)) {
            try {
                // http需要特殊路由操作，使用到容器里面的servlet-api
                PluginClassLoader.Routing[] routingArray = null;
                if (config.getPluginIdentities().contains(InvokeType.HTTP.name())) {
                    int retryTime = 60;
                    // Agent启动方式下类可能为加载完
                    while (configInfo.getMode() == Mode.AGENT && --retryTime > 0
                            && ClassloaderBridge.instance().findClassInstances(Constants.SERVLET_API_NAME).size() == 0) {
                        try {
                            log.info("http plugin required servlet-api class router,waiting for class loading");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    List<Class<?>> instances = ClassloaderBridge.instance().findClassInstances(Constants.SERVLET_API_NAME);
                    if (instances.size() > 1) {
                        throw new RuntimeException("found multiple servlet-api loaded in container, can't use http plugin");
                    }
                    if (instances.size() == 1){
                        Class<?> aClass = instances.get(0);
                        routingArray = new PluginClassLoader.Routing[]{new PluginClassLoader.Routing(aClass.getClassLoader(), "^javax.servlet..*")};
                    } else {
                        config.getPluginIdentities().remove(InvokeType.HTTP.name());
                        log.info("http plugin required servlet-api class router, but found no valid class in classloader, ignore http plugin");
                    }
                }
                String pluginsPath;
                if (StringUtils.isEmpty(config.getPluginsPath())) {
                    pluginsPath = PathUtils.getPluginPath();
                } else {
                    pluginsPath = config.getPluginsPath();
                }
                lifecycleManager = new JarFileLifeCycleManager(pluginsPath, routingArray);
                // 装载插件
                invokePlugins = lifecycleManager.loadInvokePlugins();
                ApplicationModel.instance().setConfig(config);
                for (InvokePlugin invokePlugin : invokePlugins) {
                    try {
                        if (invokePlugin.enable(config)) {
                            log.info("enable plugin {} success", invokePlugin.identity());
                            invokePlugin.watch(eventWatcher, invocationListener);
                        }
                    } catch (PluginLifeCycleException e) {
                        log.info("watch plugin occurred error", e);
                    }
                }
                // 装载回放器
                List<Repeater> repeaters = lifecycleManager.loadRepeaters();
                for (Repeater repeater : repeaters) {
                    if (repeater.enable(config)) {
                        repeater.setBroadcast(broadcaster);
                    }
                }
                RepeaterBridge.instance().build(repeaters);
                // 装载消息订阅器
                List<SubscribeSupporter> subscribes = lifecycleManager.loadSubscribes();
                for (SubscribeSupporter subscribe : subscribes) {
                    subscribe.register();
                }
                TtlConcurrentAdvice.watcher(eventWatcher).watch(config);
            } catch (Throwable throwable) {
                initialized.compareAndSet(true, false);
                log.error("error occurred when initialize module", throwable);
            }
        }
    }

    /**
     * 回放http接口
     *
     * @param req    请求参数
     * @param writer printWriter
     */
    @Command("repeat")
    public void repeat(final Map<String, String> req, final PrintWriter writer) {
        try {
            String data = req.get(Constants.DATA_TRANSPORT_IDENTIFY);
            if (StringUtils.isEmpty(data)) {
                writer.write("invalid request, cause parameter {" + Constants.DATA_TRANSPORT_IDENTIFY + "} is required");
                return;
            }
            RepeatEvent event = new RepeatEvent();
            Map<String, String> requestParams = new HashMap<String, String>(16);
            for (Map.Entry<String, String> entry : req.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
            }
            event.setRequestParams(requestParams);
            EventBusInner.post(event);
            writer.write("submit success");
        } catch (Throwable e) {
            writer.write(e.getMessage());
        }
    }

    /**
     * 配置推送接口
     *
     * @param req    请求参数
     * @param writer printWriter
     */
    @Command("pushConfig")
    public void pushConfig(final Map<String, String> req, final PrintWriter writer) {
        String data = req.get(Constants.DATA_TRANSPORT_IDENTIFY);
        if (StringUtils.isEmpty(data)) {
            writer.write("invalid request, cause parameter {" + Constants.DATA_TRANSPORT_IDENTIFY + "} is required");
            return;
        }
        try {
            RepeaterConfig config = SerializerWrapper.hessianDeserialize(data, RepeaterConfig.class);
            noticeConfigChange(config);
            writer.write("config push success");
        } catch (SerializeException e) {
            writer.write("invalid request, cause deserialize config failed, reason = {" + e.getMessage() + "}");
        }
    }

    /**
     * 通知配置变更
     *
     * @param config 配置文件
     */
    private void noticeConfigChange(final RepeaterConfig config) {
        if (initialized.get()) {
            for (InvokePlugin invokePlugin : invokePlugins) {
                try {
                    invokePlugin.onConfigChange(config);
                } catch (PluginLifeCycleException e) {
                    log.error("error occurred when notice config, plugin ={}", invokePlugin.getType().name(), e);
                }
            }
        }
    }
}
