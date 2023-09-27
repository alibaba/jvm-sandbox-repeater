package org.tony.console.common.enums;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:53
 */
public enum Status {

    INVALID(0),
    VALID(1);

    public int code;

    Status(int code) {
        this.code = code;
    }

    public static Status getByCode(int code) {
        for (Status status : Status.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }
}
