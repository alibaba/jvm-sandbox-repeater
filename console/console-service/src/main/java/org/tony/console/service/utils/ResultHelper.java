package org.tony.console.service.utils;
import org.tony.console.common.Result;

/**
 * {@link ResultHelper}
 * <p>
 *
 * @author zhaoyb1990
 */
public class ResultHelper {

    public static <T> Result<T> copy(Result result) {
        Result<T> rr = Result.builder().build();
        rr.setSuccess(result.isSuccess());
        rr.setMessage(result.getMessage());
        return rr;
    }

    public static <T> Result<T> fail(String message) {
        return Result.builder().message(message).build();
    }

    public static <T> Result<T> fail() {
        return fail("operate failed");
    }


    public static <T> Result<T> success(String message, T t) {
        return Result.builder().message(message).success(true).data(t).build();
    }
    public static <T> Result<T> success(String message) {
        return success(message, null);
    }

    public static <T> Result<T> success() {
        return success("operate success", null);
    }

    public static <T> Result<T> success(T t) {
        return success("operate success", t);
    }

    public static Result<String> fs(boolean success) {
        return success ? success() : fail();
    }
}
