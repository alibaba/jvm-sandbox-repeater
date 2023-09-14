package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/16 15:08
 */
@Data
public class AddTestCaseRequest implements BizRequest {

    private String caseName;

    private Long suitId;

    /**
     * 这里存储的是mongo的id
     */
    private List<String> recordIdList;

    private String user;

    private String appName;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(caseName, "caseName is blank");
        VerifyUtil.verifyNotBlank(user, "user is blank");
        VerifyUtil.verifyNotNull(suitId, "suitId is blank");
        VerifyUtil.verifyNotEmpty(recordIdList, "recordIdList is null");
    }
}
