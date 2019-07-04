package com.alibaba.jvm.sandbox.repeater.plugin.domain.mock;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Set;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RecordModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;



/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class MockRequest {

    private BeforeEvent event;

    private Object[] argumentArray;

    private InvokeType type;

    private RecordModel recordModel;

    private RepeatMeta meta;

    private Identity identity;

    private String traceId;

    private String repeatId;

    private int index;

    private Set<Identity> modifiedInvocationIdentity;

    @ConstructorProperties({"event", "argumentArray", "type", "recordModel", "meta", "identity", "traceId", "repeatId", "index", "modifiedInvocationIdentity"})
    MockRequest(BeforeEvent event, Object[] argumentArray, InvokeType type, RecordModel recordModel, RepeatMeta meta, Identity identity, String traceId, String repeatId, int index, Set<Identity> modifiedInvocationIdentity) {
        this.event = event;
        this.argumentArray = argumentArray;
        this.type = type;
        this.recordModel = recordModel;
        this.meta = meta;
        this.identity = identity;
        this.traceId = traceId;
        this.repeatId = repeatId;
        this.index = index;
        this.modifiedInvocationIdentity = modifiedInvocationIdentity;
    }

    public static MockRequest.MockRequestBuilder builder() {
        return new MockRequest.MockRequestBuilder();
    }
    public BeforeEvent getEvent() {
        return event;
    }

    public void setEvent(BeforeEvent event) {
        this.event = event;
    }

    public Object[] getArgumentArray() {
        return argumentArray;
    }

    public void setArgumentArray(Object[] argumentArray) {
        this.argumentArray = argumentArray;
    }

    public InvokeType getType() {
        return type;
    }

    public void setType(InvokeType type) {
        this.type = type;
    }

    public RecordModel getRecordModel() {
        return recordModel;
    }

    public void setRecordModel(RecordModel recordModel) {
        this.recordModel = recordModel;
    }

    public RepeatMeta getMeta() {
        return meta;
    }

    public void setMeta(RepeatMeta meta) {
        this.meta = meta;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set<Identity> getModifiedInvocationIdentity() {
        return modifiedInvocationIdentity;
    }

    public void setModifiedInvocationIdentity(Set<Identity> modifiedInvocationIdentity) {
        this.modifiedInvocationIdentity = modifiedInvocationIdentity;
    }

    public static class MockRequestBuilder {
        private BeforeEvent event;
        private Object[] argumentArray;
        private InvokeType type;
        private RecordModel recordModel;
        private RepeatMeta meta;
        private Identity identity;
        private String traceId;
        private String repeatId;
        private int index;
        private Set<Identity> modifiedInvocationIdentity;

        MockRequestBuilder() {
        }

        public MockRequest.MockRequestBuilder event(BeforeEvent event) {
            this.event = event;
            return this;
        }

        public MockRequest.MockRequestBuilder argumentArray(Object[] argumentArray) {
            this.argumentArray = argumentArray;
            return this;
        }

        public MockRequest.MockRequestBuilder type(InvokeType type) {
            this.type = type;
            return this;
        }

        public MockRequest.MockRequestBuilder recordModel(RecordModel recordModel) {
            this.recordModel = recordModel;
            return this;
        }

        public MockRequest.MockRequestBuilder meta(RepeatMeta meta) {
            this.meta = meta;
            return this;
        }

        public MockRequest.MockRequestBuilder identity(Identity identity) {
            this.identity = identity;
            return this;
        }

        public MockRequest.MockRequestBuilder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public MockRequest.MockRequestBuilder repeatId(String repeatId) {
            this.repeatId = repeatId;
            return this;
        }

        public MockRequest.MockRequestBuilder index(int index) {
            this.index = index;
            return this;
        }

        public MockRequest.MockRequestBuilder modifiedInvocationIdentity(Set<Identity> modifiedInvocationIdentity) {
            this.modifiedInvocationIdentity = modifiedInvocationIdentity;
            return this;
        }

        public MockRequest build() {
            return new MockRequest(this.event, this.argumentArray, this.type, this.recordModel, this.meta, this.identity, this.traceId, this.repeatId, this.index, this.modifiedInvocationIdentity);
        }

        @Override
        public String toString() {
            return "MockRequest.MockRequestBuilder(event=" + this.event + ", argumentArray=" + Arrays.deepToString(this.argumentArray) + ", type=" + this.type + ", recordModel=" + this.recordModel + ", meta=" + this.meta + ", identity=" + this.identity + ", traceId=" + this.traceId + ", repeatId=" + this.repeatId + ", index=" + this.index + ", modifiedInvocationIdentity=" + this.modifiedInvocationIdentity + ")";
        }
    }
}
