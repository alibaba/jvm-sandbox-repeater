package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2022/12/19 08:25
 */
@Data
public class RemoveTestSuitRequest implements BizRequest {

    private Long suitId;

    private String user;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(user, "user is null");
        VerifyUtil.verifyNotNull(suitId, "suitId is null");
    }
}
