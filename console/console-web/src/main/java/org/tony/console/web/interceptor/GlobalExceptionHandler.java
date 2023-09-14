package org.tony.console.web.interceptor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author peng.hu1
 * @Date 2023/3/17 13:01
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result bizException(HttpServletRequest req, BizException e) {
        return Result.buildFail(e.getMessage());
    }
}
