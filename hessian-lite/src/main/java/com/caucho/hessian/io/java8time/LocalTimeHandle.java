package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link LocalTimeHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class LocalTimeHandle implements HessianHandle, Serializable {
    private int hour;
    private int minute;
    private int second;
    private int nano;

    public LocalTimeHandle() {
    }

    public LocalTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.LocalTime");
            Method m = c.getDeclaredMethod("getHour");
            this.hour = (Integer) m.invoke(o, new Object[0]);
            m = c.getDeclaredMethod("getMinute");
            this.minute = (Integer) m.invoke(o, new Object[0]);
            m = c.getDeclaredMethod("getSecond");
            this.second = (Integer) m.invoke(o, new Object[0]);
            m = c.getDeclaredMethod("getNano");
            this.nano = (Integer) m.invoke(o, new Object[0]);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.LocalTime");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return m.invoke(null, this.hour, this.minute, this.second, this.nano);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}