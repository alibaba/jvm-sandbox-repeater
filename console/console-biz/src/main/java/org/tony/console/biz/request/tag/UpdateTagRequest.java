package org.tony.console.biz.request.tag;

import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/5/9 17:56
 */
@Data
public class UpdateTagRequest implements BizRequest {

    private Long id;

    private String nickName;

    private String name;

    private String jsonpath;

    private String identity;

    private String operator;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(nickName, "nickName is blank");
        VerifyUtil.verifyNotBlank(name, "name is blank");
        VerifyUtil.verifyNotNull(id, "id is null");
        VerifyUtil.verifyNotBlank(jsonpath, "jsonpath is blank");
        VerifyUtil.verifyNotBlank(operator, "operator is blank");
    }
}
