package org.tony.console.service.model.enums;

public enum TestSuitType {

    ROOT(0),  //根节点
    CATALOG(1), //目录
    Task(2); //叶子节点 目录

    public int code;

    TestSuitType(int code) {
        this.code = code;
    }

    public static TestSuitType getByCode(int code) {
        for (TestSuitType type : TestSuitType.values()) {
            if (type.code == code) {
                return type;
            }
        }

        return null;
    }
}
