package org.tony.console.common.domain;

public enum ReplayType {

    RECORD(0),
    TESTCASE(1);

    public int type;

    ReplayType(int type) {
        this.type = type;
    }

    public static ReplayType getByCode(int code) {
        for (ReplayType item : ReplayType.values()) {
            if (item.type == code) {
                return item;
            }
        }

        return null;
    }
}
