package org.tony.console.biz.components.createTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleInfoService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 13:18
 */
@Slf4j
@Order(10)
@Component
public class ModuleInfoCheck implements CreateTaskComponent {

    @Resource
    ModuleInfoService moduleInfoService;

    @Override
    public void execute(CreateTestTaskBizRequest request) throws BizException {
        List<String> ipList = request.getIpList();
        String environment = request.getEnvironment();

        ModuleInfoQuery moduleInfoQuery = new ModuleInfoQuery();
        moduleInfoQuery.setEnvironment(environment);
        moduleInfoQuery.setAppName(request.getAppName());
        List<ModuleInfoBO> moduleInfoBOList = moduleInfoService.queryV2(moduleInfoQuery);
        if (CollectionUtils.isEmpty(moduleInfoBOList)) {
            throw BizException.build("机器不存在");
        }

        List<ModuleInfoBO> filteredList;
        if (CollectionUtils.isEmpty(ipList)) {
            filteredList = moduleInfoBOList
                    .stream()
                    .filter(item->item.getStatus().equals(ModuleStatus.ACTIVE))
                    .collect(Collectors.toList());
        } else {
            filteredList = moduleInfoBOList
                    .stream()
                    .filter(item->ipList.contains(item.getIp()))
                    .filter(item->item.getStatus().equals(ModuleStatus.ACTIVE))
                    .collect(Collectors.toList());

        }

        if (CollectionUtils.isEmpty(filteredList)) {
            throw BizException.build("机器状态不是运行状态");
        }

        BizTemplate.getSession().addData(KEY_MODULE_LIST, filteredList);
    }

    @Override
    public boolean isSupport(CreateTestTaskBizRequest createTestTaskBizRequest) {
        return true;
    }
}
