package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/5 11:25
 */
@Data
public class CreateTestTaskBizRequest implements BizRequest {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 名称
     */
    private String name;

    /**
     * 测试任务集合
     */
    private List<Long> testTaskIdList;

    /**
     * ip列表
     */
    private List<String> ipList;

    /**
     * 环境
     */
    private String environment;

    /**
     * 期望执行时间
     */
    private Date gmtExec;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 部署信息
     */
    private String deployTaskId;

    private String deployInstName;

    /**
     * 失败重试次数
     */
    private Integer retryTime;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(appName, "appName is blank");
        VerifyUtil.verifyNotBlank(name, "name is blank");
        VerifyUtil.verifyNotBlank(creator, "creator is blank");
        VerifyUtil.verifyNotBlank(environment, "environment is null");
        //VerifyUtil.verifyNotEmpty(testTaskIdList, "taskIdList or testCaseIdList is null");
//        VerifyUtil.verifyNotEmpty(ipList, "ipList is null");
    }
}
