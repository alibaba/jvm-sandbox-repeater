package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 17:42
 */
@Data
public class AddTestCaseBizRequest implements BizRequest {

    private String caseName;

    private Long suitId;

    private List<Long> recordList;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(caseName, "caseName is null");
        VerifyUtil.verifyNotNull(suitId, "suitId is null");
        VerifyUtil.verifyNotEmpty(recordList, "recordList is null");
    }
}
