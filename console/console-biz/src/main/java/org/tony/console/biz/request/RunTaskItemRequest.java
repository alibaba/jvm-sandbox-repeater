package org.tony.console.biz.request;

import lombok.Data;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.service.model.TaskItemDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/6 11:07
 */
@Data
public class RunTaskItemRequest implements BizRequest {

    TaskDTO taskDTO;

    TaskItemDTO taskItem;

    List<ModuleInfoBO> availableModuleList;

    Boolean retry = false;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(taskDTO, "taskItemDTO is null");
        VerifyUtil.verifyNotNull(taskItem, "taskItem is empty");
    }
}
