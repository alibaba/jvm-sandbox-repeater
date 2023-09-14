package org.tony.console.service.model.enums;

public enum ResourceType {

    APP(0);  //应用

    public int code;

    ResourceType(int code) {
        this.code = code;
    }

    public static ResourceType getByCode(int code) {
        for (ResourceType status : ResourceType.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }
}
