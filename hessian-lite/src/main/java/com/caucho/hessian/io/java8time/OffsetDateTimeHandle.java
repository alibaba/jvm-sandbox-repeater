package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link OffsetDateTimeHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class OffsetDateTimeHandle implements HessianHandle, Serializable {
    private Object dateTime;
    private Object offset;

    public OffsetDateTimeHandle() {
    }

    public OffsetDateTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.OffsetDateTime");
            Method m = c.getDeclaredMethod("toLocalDateTime");
            this.dateTime = m.invoke(o);
            m = c.getDeclaredMethod("getOffset");
            this.offset = m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.OffsetDateTime");
            Method m = c.getDeclaredMethod("of", Class.forName("java.time.LocalDateTime"), Class.forName("java.time.ZoneOffset"));
            return m.invoke(null, this.dateTime, this.offset);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}
