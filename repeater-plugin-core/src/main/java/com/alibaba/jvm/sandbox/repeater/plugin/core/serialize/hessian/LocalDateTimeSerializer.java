package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.hessian;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * {@link LocalDateTimeSerializer} hessian 序列化反序列化 localDateTime 适配
 * <p>
 *
 * @author zhaoyb1990
 */
@Deprecated
public class LocalDateTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {

        if (obj == null) {
            out.writeNull();
        } else {
            Class cl = obj.getClass();

            if (out.addRef(obj)) {
                return;
            }
            // ref 返回-2 便是开始写Map
            int ref = out.writeObjectBegin(cl.getName());

            if (ref < -1) {
                out.writeString("value");
                out.writeUTCDate(((LocalDateTime) obj).toInstant(ZoneOffset.of("+8")).toEpochMilli());
                out.writeMapEnd();
            } else {
                if (ref == -1) {
                    out.writeInt(1);
                    out.writeString("value");
                    out.writeObjectBegin(cl.getName());
                }
                out.writeUTCDate(((LocalDateTime) obj).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
        }
    }
}
