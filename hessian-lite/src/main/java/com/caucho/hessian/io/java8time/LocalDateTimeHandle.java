package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link LocalDateTimeHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class LocalDateTimeHandle implements HessianHandle, Serializable {

    private Object date;
    private Object time;

    public LocalDateTimeHandle() {
    }

    public LocalDateTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.LocalDateTime");
            Method m = c.getDeclaredMethod("toLocalDate");
            this.date = m.invoke(o);
            m = c.getDeclaredMethod("toLocalTime");
            this.time = m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.LocalDateTime");
            Method m = c.getDeclaredMethod("of", Class.forName("java.time.LocalDate"), Class.forName("java.time.LocalTime"));
            return m.invoke(null, this.date, this.time);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}