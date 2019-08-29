package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link YearHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class YearHandle implements HessianHandle, Serializable {
    private int year;

    public YearHandle() {
    }

    public YearHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Year");
            Method m = c.getDeclaredMethod("getValue");
            this.year = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Year");
            Method m = c.getDeclaredMethod("of", Integer.TYPE);
            return m.invoke(null, this.year);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}