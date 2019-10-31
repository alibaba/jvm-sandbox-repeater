package com.caucho.hessian.io.java8time;


import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link YearMonthHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class YearMonthHandle implements HessianHandle, Serializable {

    private int year;
    private int month;

    public YearMonthHandle() {
    }

    public YearMonthHandle(Object o) {
        try {
            Class c = Class.forName("java.time.YearMonth");
            Method m = c.getDeclaredMethod("getYear");
            this.year = (Integer) m.invoke(o);
            m = c.getDeclaredMethod("getMonthValue");
            this.month = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.YearMonth");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE);
            return m.invoke(null, this.year, this.month);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}