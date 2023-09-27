package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;


/**
 * @author peng.hu1
 * @Date 2023/1/3 14:50
 */
@Data
public class ReplayRequest implements BizRequest {

    private String appName;

    private String caseId;

    private String ip;

    /**
     * 非必填，回放id
     */
    private String repeatId;

    /**
     * 非必填，环境
     */
    private String environment;

    /**
     * 非必填，端口
     */
    private String port;

    /**
     * V2使用
     */
    private ModuleInfoBO moduleInfoBO;

    private Long taskItemId;

    private boolean mock = true;

    private boolean isSingle = false;

    private String traceId;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is null");
        VerifyUtil.verifyNotBlank(ip, "ip is null");
        VerifyUtil.verify(caseId!=null || traceId!=null, "caseId or traceId is null");
    }
}
