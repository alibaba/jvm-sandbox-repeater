package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/9 10:47
 */
@Data
public class RemoveTestCaseBizRequest implements BizRequest  {

    /**
     * 非必填，删除taskId的item项的时候使用
     */
    private Long taskId;

    private List<String> caseIdList;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotEmpty(caseIdList, "caseIdList is empty");
    }
}
