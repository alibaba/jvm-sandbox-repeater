package org.tony.console.biz.components.createTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/2 18:26
 */
@Slf4j
@Order(1)
@Component
public class DeployTaskCheck implements CreateTaskComponent {

    @Resource
    ModuleInfoService moduleInfoService;

    @Value("${task.delay.deploy}")
    private int delayTime = 2;

    @Resource
    AppConfigService appConfigService;

    @Override
    public void execute(CreateTestTaskBizRequest request) throws BizException {

        ModuleInfoQuery moduleInfoQuery = new ModuleInfoQuery();
        moduleInfoQuery.setEnvironment(request.getEnvironment());
        moduleInfoQuery.setAppName(request.getAppName());
        List<ModuleInfoBO> moduleInfoBOList = moduleInfoService.queryV2(moduleInfoQuery);
        if (CollectionUtils.isEmpty(moduleInfoBOList)) {
            throw BizException.build("机器不存在");
        }

        List<ModuleInfoBO> filteredList = moduleInfoBOList
                .stream()
                .filter(item->item.getStatus().equals(ModuleStatus.ACTIVE))
                .collect(Collectors.toList());

        List<String> ipList = filteredList.stream().map(ModuleInfoBO::getIp).collect(Collectors.toList());
        request.setIpList(ipList);

        //延迟1分钟执行，刚部署完，应用可能需要预热
        request.setGmtExec(DateUtil.getDayWithMin(new Date(), delayTime));

        AppTestTaskSetDTO appTestTaskSetDTO = appConfigService.queryTestTaskSet(request.getAppName(), Env.fromString(request.getEnvironment()));
        if (appTestTaskSetDTO != null) {
            request.setRetryTime(appTestTaskSetDTO.getFailRetryTime());
        }
    }

    @Override
    public boolean isSupport(CreateTestTaskBizRequest request) {

        if (request.getDeployTaskId()!=null && BizTemplate.getSession().containsKey(KEY_DEPLOY_TASK)) {
            return true;
        }

        return false;
    }
}
