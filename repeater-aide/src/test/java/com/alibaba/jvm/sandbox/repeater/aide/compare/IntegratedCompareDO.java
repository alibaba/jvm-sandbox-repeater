package com.alibaba.jvm.sandbox.repeater.aide.compare;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * {@link IntegratedCompareDO}
 * <p>
 *
 * @author zhaoyb1990
 */
public class IntegratedCompareDO {

    private String name;

    private int count;

    private Character character;

    private Double salary;

    private Date time;

    private Map<String, String> extensions;

    private List<IntegratedCompareDO> integratedCompareDOS;

    private BigDecimal money;

    private LocalDateTime localDateTime;

    private IntegratedCompareDO parent;

    private Student student;

    public static IntegratedCompareDOBuilder builder() {
        return new IntegratedCompareDOBuilder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public List<IntegratedCompareDO> getIntegratedCompareDOS() {
        return integratedCompareDOS;
    }

    public void setIntegratedCompareDOS(List<IntegratedCompareDO> integratedCompareDOS) {
        this.integratedCompareDOS = integratedCompareDOS;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public IntegratedCompareDO getParent() {
        return parent;
    }

    public void setParent(IntegratedCompareDO parent) {
        this.parent = parent;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public static final class IntegratedCompareDOBuilder {
        private String name;
        private int count;
        private Character character;
        private Double salary;
        private Date time;
        private Map<String, String> extentions;
        private List<IntegratedCompareDO> integratedCompareDOS;
        private BigDecimal money;
        private LocalDateTime localDateTime;
        private IntegratedCompareDO parent;
        private Student student;

        private IntegratedCompareDOBuilder() {
        }

        public IntegratedCompareDOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IntegratedCompareDOBuilder count(int count) {
            this.count = count;
            return this;
        }

        public IntegratedCompareDOBuilder character(Character character) {
            this.character = character;
            return this;
        }

        public IntegratedCompareDOBuilder salary(Double salary) {
            this.salary = salary;
            return this;
        }

        public IntegratedCompareDOBuilder time(Date time) {
            this.time = time;
            return this;
        }

        public IntegratedCompareDOBuilder extentions(Map<String, String> extentions) {
            this.extentions = extentions;
            return this;
        }

        public IntegratedCompareDOBuilder integratedCompareDOS(List<IntegratedCompareDO> integratedCompareDOS) {
            this.integratedCompareDOS = integratedCompareDOS;
            return this;
        }

        public IntegratedCompareDOBuilder money(BigDecimal money) {
            this.money = money;
            return this;
        }

        public IntegratedCompareDOBuilder localDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }

        public IntegratedCompareDOBuilder parent(IntegratedCompareDO parent) {
            this.parent = parent;
            return this;
        }

        public IntegratedCompareDOBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public IntegratedCompareDO build() {
            IntegratedCompareDO integratedCompareDO = new IntegratedCompareDO();
            integratedCompareDO.count = this.count;
            integratedCompareDO.money = this.money;
            integratedCompareDO.parent = this.parent;
            integratedCompareDO.salary = this.salary;
            integratedCompareDO.name = this.name;
            integratedCompareDO.time = this.time;
            integratedCompareDO.student = this.student;
            integratedCompareDO.localDateTime = this.localDateTime;
            integratedCompareDO.extensions = this.extentions;
            integratedCompareDO.integratedCompareDOS = this.integratedCompareDOS;
            integratedCompareDO.character = this.character;
            return integratedCompareDO;
        }
    }
}
