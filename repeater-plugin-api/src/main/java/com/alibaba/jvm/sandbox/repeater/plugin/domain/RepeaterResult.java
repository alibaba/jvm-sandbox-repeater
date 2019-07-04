package com.alibaba.jvm.sandbox.repeater.plugin.domain;


/**
 * {@link RepeaterResult}
 * <p>
 *
 * @author zhaoyb1990
 */

public class RepeaterResult<T> {

    private boolean success;
    private T data;
    private String message;

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private boolean success;
        private T data;
        private String message;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public <T> RepeaterResult<T> build() {
            RepeaterResult<T> pr = new RepeaterResult<T>();
            pr.setMessage(this.message);
            pr.setSuccess(this.success);
            pr.setData((T) this.data);
            return pr;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
