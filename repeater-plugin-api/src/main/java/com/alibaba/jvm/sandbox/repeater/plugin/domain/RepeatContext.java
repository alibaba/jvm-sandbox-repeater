package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeatContext {
    private RepeatMeta meta;
    private RecordModel recordModel;
    private String traceId;
    private String throwableSerialized;
    private boolean canMockDate;
    private boolean singleReplay; //用于区分是否为单次回放

    public RepeatContext(RepeatMeta meta, RecordModel recordModel, String traceId, boolean singleReplay) {
        this.meta = meta;
        this.recordModel = recordModel;
        this.traceId = traceId;
        this.canMockDate = false;
        this.singleReplay = singleReplay;
    }

    public RepeatMeta getMeta() {
        return meta;
    }

    public void setMeta(RepeatMeta meta) {
        this.meta = meta;
    }

    public RecordModel getRecordModel() {
        return recordModel;
    }

    public void setRecordModel(RecordModel recordModel) {
        this.recordModel = recordModel;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getThrowableSerialized() {
        return throwableSerialized;
    }

    public void setThrowableSerialized(String throwableSerialized) {
        this.throwableSerialized = throwableSerialized;
    }

    public boolean getCanMockDate() {
        return canMockDate;
    }

    public void setCanMockDate(boolean canMockDate) {
        this.canMockDate = canMockDate;
    }

    public boolean isSingleReplay() {
        return singleReplay;
    }

    public void setSingleReplay(boolean singleReplay) {
        this.singleReplay = singleReplay;
    }
}
