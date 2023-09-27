package org.tony.console.common;


/**
 * {@link Result}
 * <p>
 *
 * @author zhaoyb1990
 */

public class Result<T> {

    private boolean success;
    private T data;
    private String message;

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static Result buildFail(String msg) {
        Result result = new Result();
        result.setData(null);
        result.setMessage(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> buildSuccess(String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage(msg);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> buildSuccess(T data, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage(msg);
        result.setData(data);
        return result;
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

        public <T> Result<T> build() {
            Result<T> pr = new Result<T>();
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
