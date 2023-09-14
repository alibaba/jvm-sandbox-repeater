package org.tony.console.biz.request.app;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;
import org.tony.console.service.model.config.AppTestTaskSetDTO;

/**
 * @author peng.hu1
 * @Date 2023/3/2 15:08
 */
@Data
public class UpdateTestSetRequest implements BizRequest {

    private AppTestTaskSetDTO appTestTaskSet;

    private String appName;

    private String env;

    /**
     * 操作人
     */
    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank");
        VerifyUtil.verifyNotBlank(env, "env is blank");
        VerifyUtil.verifyNotNull(appTestTaskSet, "appTestTaskSet is blank");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
    }
}
