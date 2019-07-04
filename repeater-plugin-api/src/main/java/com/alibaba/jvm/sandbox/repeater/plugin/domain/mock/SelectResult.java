package com.alibaba.jvm.sandbox.repeater.plugin.domain.mock;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;

import java.beans.ConstructorProperties;


/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class SelectResult {

    private boolean match;

    private Invocation invocation;

    private Long cost;

    @ConstructorProperties({"match", "invocation", "cost"})
    SelectResult(boolean match, Invocation invocation, Long cost) {
        this.match = match;
        this.invocation = invocation;
        this.cost = cost;
    }

    public static SelectResult.SelectResultBuilder builder() {
        return new SelectResult.SelectResultBuilder();
    }

    public boolean isMatch() {
        return this.match;
    }

    public Invocation getInvocation() {
        return this.invocation;
    }

    public Long getCost() {
        return this.cost;
    }

    public static class SelectResultBuilder {
        private boolean match;
        private Invocation invocation;
        private Long cost;

        SelectResultBuilder() {
        }

        public SelectResult.SelectResultBuilder match(boolean match) {
            this.match = match;
            return this;
        }

        public SelectResult.SelectResultBuilder invocation(Invocation invocation) {
            this.invocation = invocation;
            return this;
        }

        public SelectResult.SelectResultBuilder cost(Long cost) {
            this.cost = cost;
            return this;
        }

        public SelectResult build() {
            return new SelectResult(this.match, this.invocation, this.cost);
        }

        @Override
        public String toString() {
            return "SelectResult.SelectResultBuilder(match=" + this.match + ", invocation=" + this.invocation + ", cost=" + this.cost + ")";
        }
    }
}
