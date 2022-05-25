package com.alibaba.repeater.console.service.util;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;

/**
 * {@link ResultHelper}
 * <p>
 *
 * @author zhaoyb1990
 */
public class ResultHelper {

    public static <T> RepeaterResult<T> copy(RepeaterResult result) {
        RepeaterResult<T> rr = RepeaterResult.builder().build();
        rr.setSuccess(result.isSuccess());
        rr.setMessage(result.getMessage());
        return rr;
    }

    public static <T> RepeaterResult<T> fail(String message) {
        return RepeaterResult.builder().message(message).build();
    }

    public static <T> RepeaterResult<T> fail() {
        return fail("operate failed");
    }


    public static <T> RepeaterResult<T> success(String message, T t) {
        return RepeaterResult.builder().message(message).success(true).data(t).build();
    }
    public static <T> RepeaterResult<T> success(String message) {
        return success(message, null);
    }

    public static <T> RepeaterResult<T> success() {
        return success("operate success", null);
    }

    public static <T> RepeaterResult<T> success(T t) {
        return success("operate success", t);
    }

    public static RepeaterResult<String> fs(boolean success) {
        return success ? success() : fail();
    }
}
