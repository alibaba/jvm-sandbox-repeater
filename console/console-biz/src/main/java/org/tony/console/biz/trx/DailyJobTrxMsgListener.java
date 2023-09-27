package org.tony.console.biz.trx;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.service.trxMsg.ExecResult;
import org.tony.console.service.trxMsg.Topic;
import org.tony.console.service.trxMsg.TrxMsgListener;
import org.tony.console.service.trxMsg.model.DailyTestMsg;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/3/27 15:04
 */
@Slf4j
@Component
public class DailyJobTrxMsgListener implements TrxMsgListener<DailyTestMsg> {

    @Resource
    TestTaskBizService testTaskBizService;

    @Resource
    AppConfigService appConfigService;

    @Override
    public Topic getTopic() {
        return Topic.DAILY_TASK_JOB;
    }

    @Override
    public ExecResult execute(DailyTestMsg dailyTestMsg) {

        AppDailyTestConfigDTO appDailyTestConfigDTO = appConfigService.queryDailyTest(dailyTestMsg.getAppName());
        if (appDailyTestConfigDTO == null) {
            return ExecResult.SUCCESS;
        }

        AppTestTaskSetDTO appTestTaskSetDTO = appConfigService.queryTestTaskSet(dailyTestMsg.getAppName(), appDailyTestConfigDTO.getEnv());
        if (appTestTaskSetDTO == null) {
            return ExecResult.SUCCESS;
        }

        CreateTestTaskBizRequest request = new CreateTestTaskBizRequest();

        request.setCreator("SYSTEM");
        request.setAppName(dailyTestMsg.getAppName());
        request.setName("每日回归任务");
        request.setEnvironment(appDailyTestConfigDTO.getEnv().name());
        request.setTestTaskIdList(Lists.newArrayList(appTestTaskSetDTO.getTaskIdSet()));

        try {
            testTaskBizService.createTask(request);
        } catch (Exception e) {
            log.error("system error", e);
        }

        return ExecResult.SUCCESS;
    }
}
