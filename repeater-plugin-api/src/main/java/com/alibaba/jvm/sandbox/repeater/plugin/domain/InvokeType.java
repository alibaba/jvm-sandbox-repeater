package com.alibaba.jvm.sandbox.repeater.plugin.domain;


/**
 * {@link InvokeType } 定义一种调用类型
 * <p>
 *
 * @author zhaoyb1990
 */
public class InvokeType implements java.io.Serializable {

    public static InvokeType HTTP = new InvokeType("http");

    public static InvokeType JAVA = new InvokeType("java");

    public static InvokeType MYBATIS = new InvokeType("mybatis");

    public static InvokeType IBATIS = new InvokeType("ibatis");

    public static InvokeType REDIS = new InvokeType("redis");

    public static InvokeType DUBBO = new InvokeType("dubbo");

    public static InvokeType HIBERNATE = new InvokeType("hibernate");

    public static InvokeType JPA = new InvokeType("jpa");

    public static InvokeType SOCKETIO = new InvokeType("socketio");

    public static InvokeType OKHTTP = new InvokeType("okhttp");

    public static InvokeType APACHE_HTTP_CLIENT = new InvokeType("apache-http-client");

    public static InvokeType GUAVA_CACHE = new InvokeType("guava-cache");

    public static InvokeType EH_CACHE = new InvokeType("eh-cache");

    public static InvokeType CAFFEINE_CACHE = new InvokeType("caffeine-cache");

    private String name;

    public InvokeType(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvokeType that = (InvokeType) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public String toString() {
        return "InvokeType{" +
                "name='" + name + '\'' +
                '}';
    }
}