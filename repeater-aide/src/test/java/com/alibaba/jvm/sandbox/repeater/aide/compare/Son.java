package com.alibaba.jvm.sandbox.repeater.aide.compare;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
public class Son extends Father {

    private String sonName;

    private int sonAge;

    public static SonBuilder builder() {
        return new SonBuilder();
    }

    public String getSonName() {
        return sonName;
    }

    public void setSonName(String sonName) {
        this.sonName = sonName;
    }

    public int getSonAge() {
        return sonAge;
    }

    public void setSonAge(int sonAge) {
        this.sonAge = sonAge;
    }

    public static final class SonBuilder {
        private String name;
        private String sonName;
        private int fatherAge;
        private int sonAge;

        private SonBuilder() {
        }

        public SonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SonBuilder sonName(String sonName) {
            this.sonName = sonName;
            return this;
        }

        public SonBuilder fatherAge(int fatherAge) {
            this.fatherAge = fatherAge;
            return this;
        }

        public SonBuilder sonAge(int sonAge) {
            this.sonAge = sonAge;
            return this;
        }

        public Son build() {
            Son son = new Son();
            son.setFatherName(name);
            son.setSonName(sonName);
            son.setFatherAge(fatherAge);
            son.setSonAge(sonAge);
            return son;
        }
    }
}
