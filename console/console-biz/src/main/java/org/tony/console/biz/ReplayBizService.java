package org.tony.console.biz;

import org.tony.console.biz.request.ReplayRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;

public interface ReplayBizService {

    public Result replay(ReplayRequest request) throws BizException;

    /**
     * replayV2
     * @param request
     * @return
     * @throws BizException
     */
    public Result replayV2(ReplayRequest request) throws BizException;
}
