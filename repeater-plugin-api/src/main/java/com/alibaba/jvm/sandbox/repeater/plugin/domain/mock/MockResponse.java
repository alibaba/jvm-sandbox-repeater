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

    /**
     * 是否子调用找不到
     */
    public boolean isInvocationNotFund;

    @ConstructorProperties({"action", "invocation", "throwable", "isInvocationNotFund"})
    MockResponse(Action action, Invocation invocation, Throwable throwable, boolean isInvocationNotFund) {
        this.action = action;
        this.invocation = invocation;
        this.throwable = throwable;
        this.isInvocationNotFund = isInvocationNotFund;
    }

    public static MockResponseBuilder builder() {
        return new MockResponseBuilder();
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

    public boolean isInvocationNotFund() {
        return isInvocationNotFund;
    }

    public void setInvocationNotFund(boolean invocationNotFund) {
        isInvocationNotFund = invocationNotFund;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public static class MockResponseBuilder {
        private Action action;
        private Invocation invocation;
        private Throwable throwable;
        private boolean isInvocationNotFund = false;

        MockResponseBuilder() {
        }

        public MockResponseBuilder action(Action action) {
            this.action = action;
            return this;
        }

        public MockResponseBuilder invocation(Invocation invocation) {
            this.invocation = invocation;
            return this;
        }

        public MockResponseBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public MockResponseBuilder isInvocationNotFund(boolean isInvocationNotFund) {
            this.isInvocationNotFund = isInvocationNotFund;
            return this;
        }

        public MockResponse build() {
            return new MockResponse(this.action, this.invocation, this.throwable, this.isInvocationNotFund);
        }

        @Override
        public String toString() {
            return "MockResponse.MockResponseBuilder(action=" + this.action + ", invocation=" + this.invocation + ", throwable=" + this.throwable + ")";
        }
    }


}
