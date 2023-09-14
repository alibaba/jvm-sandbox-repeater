package org.tony.console.biz.request.app;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;

/**
 * @author peng.hu1
 * @Date 2023/3/27 12:04
 */
@Data
public class UpdateDailyTestRequest implements BizRequest {

    AppDailyTestConfigDTO appDailyTest;

    private String appName;

    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(appDailyTest, "appDailyTest is null");
        VerifyUtil.verifyNotBlank(appName, "appName is blank");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
    }
}
