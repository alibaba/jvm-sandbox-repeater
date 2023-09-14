package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/16 14:05
 */
@Data
public class MoveTestCaseBizRequest implements BizRequest {

    private Long suitId;

    private List<String> caseIdList;

    private String appName;

    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
        VerifyUtil.verifyNotNull(suitId, "suitId is null");
        VerifyUtil.verifyNotEmpty(caseIdList, "caseIdList is empty");
    }
}
