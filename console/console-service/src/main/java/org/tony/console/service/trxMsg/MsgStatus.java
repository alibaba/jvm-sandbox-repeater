package org.tony.console.service.trxMsg;

public enum MsgStatus {
    INIT(0),
    SUCCESS(1),
    FAIL_TO_RETRY(2),
    FAIL(3);

    public int code;

    MsgStatus(int code) {
        this.code = code;
    }

    public static MsgStatus get(int code) {
        for (MsgStatus item : MsgStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }

        return null;
    }
}
