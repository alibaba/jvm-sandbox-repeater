package com.caucho.hessian.io.java8time;

import com.caucho.hessian.io.HessianHandle;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * {@link LocaleHandle}
 * <p>
 *
 * @author zhaoyb1990
 */
public class LocaleHandle implements HessianHandle, Serializable {

    private String language;
    private String country;
    private String variant;

    public LocaleHandle() {
    }

    public LocaleHandle(Object o) {
        try {
            Locale locale = (Locale) o;
            this.language = locale.getLanguage();
            this.country = locale.getCountry();
            this.variant = locale.getVariant();
        } catch (Throwable t) {
            // ignore
        }
    }

    private Object readResolve() {
        try {
            Method method = MethodUtils.getMatchingMethod(Locale.class, "getInstance", String.class, String.class, String.class);
            boolean accessible = method.isAccessible();
            method.setAccessible(true);
            Object locale = method.invoke(null, language, country, variant);
            method.setAccessible(accessible);
            return locale;
        } catch (Throwable t) {
            // ignore
            return new Locale(language, country, variant);
        }
    }
}