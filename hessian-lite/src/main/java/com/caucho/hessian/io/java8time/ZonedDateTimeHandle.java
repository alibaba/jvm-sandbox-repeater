package com.caucho.hessian.io.java8time;


import com.caucho.hessian.io.HessianHandle;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link ZonedDateTimeHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("unchecked")
public class ZonedDateTimeHandle implements HessianHandle, Serializable {
    private Object dateTime;
    private Object offset;
    private String zoneId;

    public ZonedDateTimeHandle() {
    }

    public ZonedDateTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.ZonedDateTime");
            Method m = c.getDeclaredMethod("toLocalDateTime");
            this.dateTime = m.invoke(o);
            m = c.getDeclaredMethod("getOffset");
            this.offset = m.invoke(o);
            m = c.getDeclaredMethod("getZone");
            Object zone = m.invoke(o);
            if (zone != null) {
                Class zoneId = Class.forName("java.time.ZoneId");
                m = zoneId.getDeclaredMethod("getId");
                this.zoneId = ((String) m.invoke(zone));
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Class zoneDateTime = Class.forName("java.time.ZonedDateTime");
            Method ofLocal = zoneDateTime.getDeclaredMethod("ofLocal", Class.forName("java.time.LocalDateTime"),
                    Class.forName("java.time.ZoneId"), Class.forName("java.time.ZoneOffset"));
            Class c = Class.forName("java.time.ZoneId");
            Method of = c.getDeclaredMethod("of", String.class);
            return ofLocal.invoke(null, this.dateTime, of.invoke(null, this.zoneId), this.offset);
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}
