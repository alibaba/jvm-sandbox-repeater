package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/1/12 17:23
 */
@Data
public class ReplaceRespRequest implements BizRequest {

    /**
     * 用例id
     */
    private String caseId;

    /**
     * 回放id
     */
    private String repeatId;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(caseId, "caseId is null");
        VerifyUtil.verifyNotBlank(repeatId, "repeatId is null");
    }
}
