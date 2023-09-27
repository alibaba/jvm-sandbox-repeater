package org.tony.console.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.TrxMessageMapper;
import org.tony.console.db.model.TrxMsgDO;
import org.tony.console.service.convert.TrxMsgConvert;
import org.tony.console.service.trxMsg.MsgStatus;
import org.tony.console.service.trxMsg.TrxMsg;
import org.tony.console.service.trxMsg.TrxMsgService;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author peng.hu1
 * @Date 2023/3/27 11:01
 */
@Slf4j
@Component
public class TrxMsgJob {

    @Resource
    TrxMsgService trxMsgService;

    @Resource
    TrxMessageMapper trxMsgMapper;

    @Resource
    TrxMsgConvert trxMsgConvert;

    @Scheduled(cron = "0/2 * * * * ?")
    public void execute() {
        Map<String, Object> params = new HashMap<>();
        params.put("statusList", Arrays.asList(MsgStatus.INIT.code, MsgStatus.FAIL_TO_RETRY.code));
        params.put("gmtExecLt", new Date());

        try {
            List<TrxMsgDO> trxMsgDOS = trxMsgMapper.query(params);

            for (TrxMsgDO trxMsgDO : trxMsgDOS) {
                TrxMsg trxMsg = trxMsgConvert.convert(trxMsgDO);
                trxMsgService.execMsg(trxMsg);
            }
        } catch (Exception e) {
            log.error("system error", e);
        }

    }
}
