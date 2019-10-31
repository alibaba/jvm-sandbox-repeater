package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link LocalDateHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class LocalDateHandle implements HessianHandle, Serializable {

    private int year;
    private int month;
    private int day;

    public LocalDateHandle() {
    }

    public LocalDateHandle(Object o) {
        try {
            Class c = Class.forName("java.time.LocalDate");
            Method m = c.getDeclaredMethod("getYear");
            this.year = (Integer) m.invoke(o);
            m = c.getDeclaredMethod("getMonthValue");
            this.month = (Integer) m.invoke(o);
            m = c.getDeclaredMethod("getDayOfMonth");
            this.day = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    public Object readResolve() {
        try {
            Class c = Class.forName("java.time.LocalDate");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return m.invoke(null, this.year, this.month, this.day);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}