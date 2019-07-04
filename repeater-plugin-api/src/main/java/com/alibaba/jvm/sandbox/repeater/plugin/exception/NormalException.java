package com.alibaba.jvm.sandbox.repeater.plugin.exception;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class NormalException extends Exception {

    public NormalException(String message) {
        super(message);
    }

    public NormalException(String message, Throwable cause) {
        super(message, cause);
    }
}
