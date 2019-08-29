package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.hessian;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.IOExceptionWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * {@link LocalDateTimeDeserializer} hessian  localDateTime deserializer
 * <p>
 *
 * @author zhaoyb1990.
 */
@Deprecated
public class LocalDateTimeDeserializer extends AbstractDeserializer {

    @Override
    public Object readObject(AbstractHessianInput in,
                             Object[] fields)
            throws IOException {

        String[] fieldNames = (String[]) fields;

        int ref = in.addRef(null);

        long initValue = Long.MIN_VALUE;

        for (int i = 0; i < fieldNames.length; i++) {
            String key = fieldNames[i];
            if ("value".equals(key)) {
                initValue = in.readUTCDate();
            } else {
                in.readObject();
            }
        }
        Object value = create(initValue);
        in.setRef(ref, value);
        return value;
    }

    private Object create(long initValue)
            throws IOException {
        if (initValue == Long.MIN_VALUE) {
            throw new IOException("java.time.LocalDateTime expects name");
        }
        try {
            return LocalDateTime.ofEpochSecond(initValue / 1000,
                    (int) (initValue % 1000) * 1000 * 1000, ZoneOffset.of("+8"));
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
