package org.tony.console.web.model;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2022/12/1 16:15
 */
@Data
public class UpdateDynamicConfigRequest implements BizRequest {

    private String appName;

    private String env;

    private DynamicConfig dynamicConfig;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank!");
        VerifyUtil.verifyNotBlank(env, "env is blank!");
        VerifyUtil.verifyNotNull(dynamicConfig, "dynamicConfig is null!");
    }
}
