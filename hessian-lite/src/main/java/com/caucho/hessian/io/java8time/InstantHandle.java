package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link InstantHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class InstantHandle implements HessianHandle, Serializable {

    private long seconds;
    private int nanos;

    public InstantHandle() {
    }

    public InstantHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Instant");
            Method m = c.getDeclaredMethod("getEpochSecond");
            this.seconds = (Long) m.invoke(o);
            m = c.getDeclaredMethod("getNano");
            this.nanos = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Instant");
            Method m = c.getDeclaredMethod("ofEpochSecond", Long.TYPE, Long.TYPE);
            return m.invoke(null, this.seconds, this.nanos);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}