package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class HessianSerializeDomain implements java.io.Serializable {

    private BigDecimal money = new BigDecimal(System.currentTimeMillis());

    private Locale locale = Locale.getDefault();

    private LocalDateTime localDateTime = LocalDateTime.now();

    private LocalDate localDate = LocalDate.now();

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HessianSerializeDomain domain = (HessianSerializeDomain) o;
        return Objects.equals(money, domain.money) &&
                Objects.equals(locale, domain.locale) &&
                Objects.equals(localDateTime, domain.localDateTime) &&
                Objects.equals(localDate, domain.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(money, locale, localDateTime, localDate);
    }
}
