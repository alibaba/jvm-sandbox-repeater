package org.tony.console.web.model;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;
import org.tony.console.service.model.AppCompareConfigDO;

/**
 * @author peng.hu1
 * @Date 2022/12/1 16:15
 */
@Data
public class UpdateCompareConfigRequest implements BizRequest {

    private String appName;

    private String env;

    private AppCompareConfigDO appCompareConfig;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank!");
        VerifyUtil.verifyNotBlank(env, "env is blank!");
        VerifyUtil.verifyNotNull(appCompareConfig, "appCompareConfig is null!");
    }
}
