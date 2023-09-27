package org.tony.console.biz.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;
import org.tony.console.service.trxMsg.Topic;
import org.tony.console.service.trxMsg.TrxMsg;
import org.tony.console.service.trxMsg.TrxMsgService;
import org.tony.console.service.trxMsg.model.DailyTestMsg;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;


/**
 * @author peng.hu1
 * @Date 2023/3/27 13:07
 */
@Slf4j
@Component
public class DailyTestBizJob {

    @Resource
    AppConfigService appConfigService;

    @Resource
    TrxMsgService trxMsgService;


    @Scheduled(cron = "0 1 0 * * ?")
    public void execute() throws ParseException {
        Map<String, AppDailyTestConfigDTO> configMap = appConfigService.queryDailyTestConfig();
        if (MapUtils.isEmpty(configMap)) {
            return;
        }

        for (String appName : configMap.keySet()) {

            AppDailyTestConfigDTO config = configMap.get(appName);
            if (config.getOpen()) {
                try {
                    trxMsgService.publishMsg(build(appName, DateUtil.getDate(config.getTime())));
                } catch (Exception e) {
                    log.error("system error", e);
                }

            }
        }
    }

    public TrxMsg<DailyTestMsg> build(String appName, Date gmtExec) {
        TrxMsg<DailyTestMsg> trxMsg = new TrxMsg<DailyTestMsg>();
        trxMsg.setTopic(Topic.DAILY_TASK_JOB);
        trxMsg.setGmtExec(gmtExec);

        DailyTestMsg content = new DailyTestMsg();
        content.setAppName(appName);

        trxMsg.setContent(content);

        return trxMsg;
    }
}
