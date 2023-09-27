package org.tony.console.common.enums;

public enum TaskStatus {

    INIT(0),  //初始化
    RUNNING(1), //奔跑中
    SUCCESS(2), //成功
    FAIL(3), //失败
    STOP(4), //终止
    FAIL_NEED_RETRY(6); //可重试失败

    public int code;

    TaskStatus(int code) {
        this.code = code;
    }

    public static TaskStatus getByCode(int code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }
}
