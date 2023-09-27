package org.tony.console.biz.request.app;

import lombok.Getter;
import lombok.Setter;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/2 10:10
 */
@Getter
@Setter
public class UpdateAdminRequest implements BizRequest {

    /**
     * 应用名
     */
    private String appName;

    /**
     * 管理员列表
     */
    private List<String> admins;

    /**
     * buId
     */
    private Integer buId;

    /**
     * 操作人
     */
    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotEmpty(admins, "admins is null");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
        VerifyUtil.verifyNotNull(buId, "buId is blank");
    }
}
