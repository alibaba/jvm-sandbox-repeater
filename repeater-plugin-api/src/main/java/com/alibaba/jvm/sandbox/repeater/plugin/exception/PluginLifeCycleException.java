package com.alibaba.jvm.sandbox.repeater.plugin.exception;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class PluginLifeCycleException extends NormalException {

    public PluginLifeCycleException(String message) {
        super(message);
    }

    public PluginLifeCycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
