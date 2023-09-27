package org.tony.console.common.request;

import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/1 19:33
 */
public interface BizRequest {

    public void check() throws BizException;
}
