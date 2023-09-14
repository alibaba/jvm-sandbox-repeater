package org.tony.console.biz.components.saveRepeat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.ReplayDao;
import org.tony.console.db.model.Replay;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/10 17:33
 */
@Slf4j
@Order(50)
@Component
public class SaveToDBOfRepeat implements SaveRepeatComponent {

    @Resource
    ReplayDao replayDao;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        Replay replay = saveRepeatRequest.getReplay();
        Replay calllback = replayDao.saveAndFlush(replay);

        applicationContext.publishEvent(replay);
    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
