package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link MonthDayHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class MonthDayHandle implements HessianHandle, Serializable {

    private int month;
    private int day;

    public MonthDayHandle() {
    }

    public MonthDayHandle(Object o) {
        try {
            Class c = Class.forName("java.time.MonthDay");
            Method m = c.getDeclaredMethod("getMonthValue");
            this.month = (Integer) m.invoke(o, new Object[0]);
            m = c.getDeclaredMethod("getDayOfMonth");
            this.day = (Integer) m.invoke(o, new Object[0]);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.MonthDay");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE);
            return m.invoke(null, this.month, this.day);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}