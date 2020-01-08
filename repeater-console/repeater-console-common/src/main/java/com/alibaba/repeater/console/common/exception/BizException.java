package com.alibaba.repeater.console.common.exception;

/**
 * {@link BizException}
 * <p>
 *
 * @author zhaoyb1990
 */
public class BizException extends Exception {

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
