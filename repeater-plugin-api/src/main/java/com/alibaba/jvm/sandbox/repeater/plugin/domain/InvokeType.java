package com.alibaba.jvm.sandbox.repeater.plugin.domain;


/**
 * {@link InvokeType } 定义一种调用类型
 * <p>
 *
 * @author zhaoyb1990
 */
public interface InvokeType extends java.io.Serializable {

    InvokeType HTTP = new InvokeType() {
        @Override
        public String name() {
            return "http";
        }
    };

    InvokeType JAVA = new InvokeType() {
        @Override
        public String name() {
            return "java";
        }
    };

    InvokeType MYBATIS = new InvokeType() {
        @Override
        public String name() {
            return "mybatis";
        }
    };

    InvokeType IBATIS = new InvokeType() {
        @Override
        public String name() {
            return "ibatis";
        }
    };

    InvokeType REDIS = new InvokeType() {
        @Override
        public String name() {
            return "redis";
        }
    };

    InvokeType DUBBO = new InvokeType() {
        @Override
        public String name() {
            return "dubbo";
        }
    };

    /**
     * 调用类型名称
     *
     * @return name
     */
    String name();
}