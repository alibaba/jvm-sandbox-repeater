package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;

/**
 * @author peng.hu1
 * @Date 2023/1/6 14:36
 */
@Data
public class UpdateTaskItemRequest implements BizRequest {

    /**
     * 子任务的id
     */
    private Long taskItemId;

    /**
     * 回放id
     */
    private String repeatId;

    private Long cost;

    @Override
    public void check() throws BizException {

    }
}
