package org.tony.console.biz.components.runTaskItem;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.tony.console.biz.Constant;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.RunTaskItemRequest;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.enums.Env;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskDTO;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/6 11:11
 */
@Order(1)
@Component
public class AvailableModuleQuery implements RunTaskItemComponent {

    @Resource
    ModuleInfoService moduleInfoService;

    @Override
    public void execute(RunTaskItemRequest runTaskItemRequest) throws BizException {
        TaskDTO taskDTO = runTaskItemRequest.getTaskDTO();

        String ipSetString = taskDTO.getExtend().getString(Constant.IP_SET);
        List<String> ipList = Arrays.asList(ipSetString.split(","));


        ModuleInfoQuery query = new ModuleInfoQuery();
        query.setAppName(taskDTO.getAppName());

        List<ModuleInfoBO> moduleInfoBOList = moduleInfoService.queryV2(query);

        List<ModuleInfoBO> filteredModuleList = moduleInfoBOList.stream()
                .filter(item->item.getStatus().equals(ModuleStatus.ACTIVE))
                .filter(item->ipList.contains(item.getIp()))
                .collect(Collectors.toList());

        //说明所有机器下线了
        if (CollectionUtils.isEmpty(filteredModuleList)) {
            //随机选取可用的
            filteredModuleList = moduleInfoBOList.stream()
                    .filter(item->item.getStatus().equals(ModuleStatus.ACTIVE))
                    .filter(item-> Env.TEST.equals(item.getEnvironment())) //写死test环境
                    .collect(Collectors.toList());

        }

        runTaskItemRequest.setAvailableModuleList(filteredModuleList);
    }

    @Override
    public boolean isSupport(RunTaskItemRequest runTaskItemRequest) {
        return true;
    }
}
