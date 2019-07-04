package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.NormalException;

/**
 * {@link SerializeException} 序列化异常
 * <p>
 *
 * @author zhaoyb1990
 */
public class SerializeException extends NormalException {

    public SerializeException(String message) {
        super(message);
        ApplicationModel.instance().exceptionOverflow(this);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
        ApplicationModel.instance().exceptionOverflow(this);
    }
}