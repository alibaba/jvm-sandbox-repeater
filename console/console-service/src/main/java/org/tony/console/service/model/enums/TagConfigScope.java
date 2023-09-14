package org.tony.console.service.model.enums;

public enum TagConfigScope {

    ALL(0),  //所有的主调用
    ONLY(1); //根据条件锚定的

    public int code;

    TagConfigScope(int code) {
        this.code = code;
    }

    public static TagConfigScope getByCode(int code) {
        for (TagConfigScope type : TagConfigScope.values()) {
            if (type.code == code) {
                return type;
            }
        }

        return null;
    }
}
