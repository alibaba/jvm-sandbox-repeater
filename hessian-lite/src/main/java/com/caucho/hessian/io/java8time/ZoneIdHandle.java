package com.caucho.hessian.io.java8time;


import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link ZoneIdHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class ZoneIdHandle implements HessianHandle, Serializable {

    private String zoneId;

    public ZoneIdHandle() {
    }

    public ZoneIdHandle(Object o) {
        try {
            Class c = Class.forName("java.time.ZoneId");
            Method m = c.getDeclaredMethod("getId");
            this.zoneId = ((String) m.invoke(o, new Object[0]));
        } catch (Throwable t) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.ZoneId");
            Method m = c.getDeclaredMethod("of", String.class);
            return m.invoke(null, this.zoneId);
        } catch (Throwable t) {
        }
        return null;
    }
}

