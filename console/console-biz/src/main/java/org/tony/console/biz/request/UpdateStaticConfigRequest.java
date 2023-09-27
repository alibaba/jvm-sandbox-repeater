package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.biz.model.StaticConfigVO;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/2/23 09:28
 */
@Data
public class UpdateStaticConfigRequest implements BizRequest {

    private String appName;

    private String env;

    private StaticConfigVO config;

    /**
     * 操作人
     */
    private String user;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank!");
        VerifyUtil.verifyNotBlank(env, "env is blank!");
        VerifyUtil.verifyNotBlank(user, "user is blank!");
        VerifyUtil.verifyNotNull(config, "staticConfig is null!");
    }
}
