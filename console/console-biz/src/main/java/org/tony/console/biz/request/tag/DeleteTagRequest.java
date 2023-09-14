package org.tony.console.biz.request.tag;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/5/9 19:04
 */
@Data
public class DeleteTagRequest implements BizRequest {

    private Long id;

    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(id, "id is null");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
    }
}
