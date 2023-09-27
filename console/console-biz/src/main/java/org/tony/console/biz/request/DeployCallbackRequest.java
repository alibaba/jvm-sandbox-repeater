package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/2/27 17:44
 */
@Data
public class DeployCallbackRequest implements BizRequest {

    private String id;

    private String uid;

    private String taskId;

    private String serviceName;

    private String instanceName;

    private String version;

    private String createdBy;

    private String ts;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(id, "id is blank");
        VerifyUtil.verifyNotBlank(instanceName, "instanceName is null");
    }
}
