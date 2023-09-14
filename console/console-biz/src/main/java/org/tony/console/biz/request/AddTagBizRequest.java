package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/3/17 11:20
 */
@Data
public class AddTagBizRequest  implements BizRequest {

    private String name;

    private String nickName;

    private String jsonPath;

    private String appName;

    private String identity;

    private Integer scope;

    private String user;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(name, "name is null");
        VerifyUtil.verifyNotBlank(nickName, "nickName is null");
        VerifyUtil.verifyNotBlank(jsonPath, "jsonPath is null");
        VerifyUtil.verifyNotBlank(appName, "appName is null");
        VerifyUtil.verifyNotBlank(identity, "identity is null");
        VerifyUtil.verifyNotNull(scope, "scope is null");
    }
}
