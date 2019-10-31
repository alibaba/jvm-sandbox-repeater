package com.caucho.hessian.io.java8time;


import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link ZoneOffsetHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class ZoneOffsetHandle implements HessianHandle, Serializable {

    private int seconds;

    public ZoneOffsetHandle() {
    }

    public ZoneOffsetHandle(Object o) {
        try {
            Class c = Class.forName("java.time.ZoneOffset");
            Method m = c.getDeclaredMethod("getTotalSeconds");
            this.seconds = (Integer) m.invoke(o, new Object[0]);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.ZoneOffset");
            Method m = c.getDeclaredMethod("ofTotalSeconds", Integer.TYPE);
            return m.invoke(null, this.seconds);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}