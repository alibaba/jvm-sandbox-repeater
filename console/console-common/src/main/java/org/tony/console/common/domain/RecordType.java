package org.tony.console.common.domain;

public enum RecordType {

    HTTP("http"),
    JAVA("java");

    public String type;

    RecordType(String type) {
        this.type = type;
    }

    public static RecordType getByString(String type) {
        for (RecordType recordType: RecordType.values()) {
            if (recordType.type.equals(type)) {
                return recordType;
            }
        }

        return null;
    }
}
