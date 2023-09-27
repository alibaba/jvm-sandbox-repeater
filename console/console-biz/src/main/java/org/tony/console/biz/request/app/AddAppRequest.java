package org.tony.console.biz.request.app;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/20 12:10
 */
@Data
public class AddAppRequest implements BizRequest {

    private String appName;

    private Long appId;

    private List<String> admins;

    private Integer buId;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is null");
        VerifyUtil.verifyNotEmpty(admins, "admins is null");
        VerifyUtil.verifyNotNull(appId, "appId is null");
        VerifyUtil.verifyNotNull(buId, "buId is null");
    }
}
