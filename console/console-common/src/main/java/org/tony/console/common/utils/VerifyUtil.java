package org.tony.console.common.utils;

import org.tony.console.common.exception.BizException;

import java.util.Collection;

/**
 * @author peng.hu1
 * @Date 2022/12/1 19:34
 */
public class VerifyUtil {

    public static void verifyNotBlank(String t, String msg) throws BizException {
        verify(t!=null && t.length()>0, msg);
    }

    public static <T> T verifyNotNull(T reference, String errorMessage) throws BizException {
        verify(reference != null, errorMessage);
        return reference;
    }

    public static <T> Collection<T> verifyNotEmpty(Collection<T> reference, String errorMessage) throws BizException {
        verify(reference != null && reference.size()>0, errorMessage);
        return reference;
    }

    public static void verify(boolean expression, String errorMessage) throws BizException {
        if (!expression) {
            throw new BizException(errorMessage);
        }
    }
}
