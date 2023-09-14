package org.tony.console.service.model.enums;

public enum Role {
    SUPER_Admin(0),  //超管
    ADMIN(1); //普通管理员

    public int code;

    Role(int code) {
        this.code = code;
    }

    public static Role getByCode(int code) {
        for (Role status : Role.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }
}
