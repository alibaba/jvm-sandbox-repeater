package com.alibaba.repeater.console.common.domain;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * {@link Regress} 回放示例
 * <p>
 *
 * @author zhaoyb1990
 */
public class Regress implements java.io.Serializable {
    private Long timestamp;
    private String name;
    private Integer index;
    private LocalDate localDate = LocalDate.now();
    private LocalDateTime localDateTime = LocalDateTime.now();
    private Locale locale = Locale.getDefault();
    private BigDecimal money = new BigDecimal(System.currentTimeMillis());

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }


    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
