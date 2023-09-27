package org.tony.console.biz.request.app;

import lombok.Getter;
import lombok.Setter;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

/**
 * @author peng.hu1
 * @Date 2023/3/29 14:22
 */
@Setter
@Getter
public class SaveGroovyContentRequest implements BizRequest {

    private Long id;

    private String content;

    private int version;

    private String user;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotBlank(content, "content is null");
        VerifyUtil.verifyNotBlank(user, "user is null");
        VerifyUtil.verifyNotNull(id, "id is null");
    }
}
