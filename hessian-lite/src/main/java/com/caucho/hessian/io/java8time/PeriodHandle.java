package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link PeriodHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class PeriodHandle implements HessianHandle, Serializable {

    private int years;
    private int months;
    private int days;

    public PeriodHandle() {
    }

    public PeriodHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Period");
            Method m = c.getDeclaredMethod("getYears");
            this.years = (Integer) m.invoke(o);
            m = c.getDeclaredMethod("getMonths");
            this.months = ((Integer) m.invoke(o));
            m = c.getDeclaredMethod("getDays");
            this.days = (Integer) m.invoke(o);
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Period");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return m.invoke(null, this.years, this.months, this.days);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}