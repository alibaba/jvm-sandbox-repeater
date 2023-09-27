package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/1/28 17:13
 */
@Data
public class RunTestTaskFailBizRequest implements BizRequest {

    private Long taskId;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(taskId, "taskId is null");
    }
}
