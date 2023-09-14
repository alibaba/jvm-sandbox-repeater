package org.tony.console.service.model.enums;

public enum TaskType {

    TEMP_TASK_LIST(0),  //临时任务
    DEPLOY(1);  //部署任务

    public int code;

    TaskType(int code) {
        this.code = code;
    }

    public static TaskType getByCode(int code) {
        for (TaskType status : TaskType.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }
}
