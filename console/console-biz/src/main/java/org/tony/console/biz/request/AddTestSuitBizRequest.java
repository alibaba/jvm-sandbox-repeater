package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2022/12/15 19:05
 */
@Data
public class AddTestSuitBizRequest implements BizRequest {

    private Long parentId;

    private String name;

    private String appName;

    private int type;

    private String user;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(name, "name is null");
        VerifyUtil.verifyNotBlank(user, "user is null");
        VerifyUtil.verifyNotNull(parentId, "parentId is null");
        VerifyUtil.verifyNotBlank(appName, "appName is null");
    }
}
