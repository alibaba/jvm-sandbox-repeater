package com.alibaba.jvm.sandbox.repeater.plugin.core;

import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.api.ConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone.StandaloneBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone.StandaloneConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.PropertyUtil;

/**
 * {@link StandaloneSwitch} 开关切换；用于切换standalone模式和服务端模式
 * <p>
 * 本地模式和远程模式的差别在于；配置拉取 和 消息投递，两个服务抽象独立的模式
 * <p>
 *
 * @author zhaoyb1990
 */
public class StandaloneSwitch {

    private static StandaloneSwitch instance = new StandaloneSwitch();

    private ConfigManager configManager;

    private Broadcaster broadcaster;

    public static StandaloneSwitch instance(){
        return instance;
    }

    private StandaloneSwitch() {
        boolean standaloneMode = Boolean.valueOf(PropertyUtil.getPropertyOrDefault(Constants.REPEAT_STANDALONE_MODE, "false"));
        if (standaloneMode) {
            broadcaster = new StandaloneBroadcaster();
            configManager = new StandaloneConfigManager();
        } else {
            configManager = new DefaultConfigManager();
            broadcaster = new DefaultBroadcaster();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }
}

