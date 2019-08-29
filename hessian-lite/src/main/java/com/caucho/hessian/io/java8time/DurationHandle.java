package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link DurationHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class DurationHandle implements HessianHandle, Serializable {

    private long seconds;
    private int nanos;

    public DurationHandle() {
    }

    public DurationHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Duration");
            Method m = c.getDeclaredMethod("getSeconds");
            this.seconds = (Long) m.invoke(o);
            m = c.getDeclaredMethod("getNano");
            this.nanos = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Duration");
            Method m = c.getDeclaredMethod("ofSeconds", Long.TYPE, Long.TYPE);
            return m.invoke(null, this.seconds, this.nanos);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}