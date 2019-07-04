package com.alibaba.jvm.sandbox.repeater.plugin.domain.mock;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;

import java.beans.ConstructorProperties;


/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class MockResponse {

    /**
     * 执行的动作
     */
    public Action action;

    /**
     * 调用
     */
    public Invocation invocation;

    /**
     * 需要抛出的异常
     */
    public Throwable throwable;

    @ConstructorProperties({"action", "invocation", "throwable"})
    MockResponse(MockResponse.Action action, Invocation invocation, Throwable throwable) {
        this.action = action;
        this.invocation = invocation;
        this.throwable = throwable;
    }

    public static MockResponse.MockResponseBuilder builder() {
        return new MockResponse.MockResponseBuilder();
    }

    public enum Action {
        /**
         * 立即返回
         */
        RETURN_IMMEDIATELY,

        /**
         * 立即抛出异常
         */
        THROWS_IMMEDIATELY,

        /**
         * 立即跳过
         */
        SKIP_IMMEDIATELY
    }

    public Action getAction() {
        return action;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static class MockResponseBuilder {
        private MockResponse.Action action;
        private Invocation invocation;
        private Throwable throwable;

        MockResponseBuilder() {
        }

        public MockResponse.MockResponseBuilder action(MockResponse.Action action) {
            this.action = action;
            return this;
        }

        public MockResponse.MockResponseBuilder invocation(Invocation invocation) {
            this.invocation = invocation;
            return this;
        }

        public MockResponse.MockResponseBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public MockResponse build() {
            return new MockResponse(this.action, this.invocation, this.throwable);
        }

        @Override
        public String toString() {
            return "MockResponse.MockResponseBuilder(action=" + this.action + ", invocation=" + this.invocation + ", throwable=" + this.throwable + ")";
        }
    }
}
