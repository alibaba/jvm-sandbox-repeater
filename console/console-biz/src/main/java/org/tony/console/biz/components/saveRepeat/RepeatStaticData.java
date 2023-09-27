package org.tony.console.biz.components.saveRepeat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Replay;
import org.tony.console.service.AppStaticService;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/2/26 10:36
 */
@Slf4j
@Order(100)
@Component
public class RepeatStaticData implements SaveRepeatComponent {

    @Resource
    AppStaticService appStaticService;

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        Replay replay = saveRepeatRequest.getReplay();

        try {
            appStaticService.increaseReplayNum(replay.getAppName(), 1);
        } catch (Exception e) {
            log.error("system error", e);
        }
    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
