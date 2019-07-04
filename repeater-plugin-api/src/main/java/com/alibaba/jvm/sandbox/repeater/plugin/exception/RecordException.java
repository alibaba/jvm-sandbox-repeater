package com.alibaba.jvm.sandbox.repeater.plugin.exception;

/**
 * {@link RecordException} 录制异常
 * <p>
 *
 * @author zhaoyb1990
 */
public class RecordException extends NormalException {

    public RecordException(String message) {
        super(message);
    }

    public RecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
