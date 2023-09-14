package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/18 10:44
 */
@Data
public class QueryTestCaseRequest implements BizRequest {

    private List<Long> taskList;

    private String appName;

    private int page=1;

    private int pageSize=10;

    private String caseName;

    private String caseId;

    private String entranceDesc;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is null");
    }
}
