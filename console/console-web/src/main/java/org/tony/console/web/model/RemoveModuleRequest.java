package org.tony.console.web.model;

import lombok.Data;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/13 09:24
 */
@Data
public class RemoveModuleRequest implements BizRequest {

    private List<ModuleInfoBO> moduleInfoBOList;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(moduleInfoBOList, "moduleInfoBOList is null");
    }
}
