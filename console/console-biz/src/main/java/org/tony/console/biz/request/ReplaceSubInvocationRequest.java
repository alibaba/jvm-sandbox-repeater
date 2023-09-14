package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * 子调用替换
 * @author peng.hu1
 * @Date 2023/1/12 20:45
 */
@Data
public class ReplaceSubInvocationRequest implements BizRequest {

    /**
     * 用例id
     */
    private String caseId;

    /**
     * 回放id
     */
    private String repeatId;

    /**
     * 子调用id
     */
    private String identity;

    /**
     * 索引
     */
    private int index;


    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(caseId, "caseId is null");
        VerifyUtil.verifyNotBlank(repeatId, "repeatId is null");
        VerifyUtil.verifyNotBlank(identity, "identityId is null");
    }
}
