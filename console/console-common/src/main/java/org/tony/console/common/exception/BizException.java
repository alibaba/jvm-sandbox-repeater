package org.tony.console.common.exception;

/**
 * @author peng.hu1
 * @Date 2022/12/1 19:22
 */
public class BizException extends Exception {
    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(String msg, Throwable e) {
        super(msg, e);
    }

    public static BizException build(String msg) {
        return new BizException(msg);
    }

    public static BizException build(String msg, Throwable e) {
        return new BizException(msg, e);
    }
}
