package org.tony.console.biz.components.saveRepeat;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.domain.ReplayStatus;
import org.tony.console.common.domain.ReplayType;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.dao.ReplayDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.service.TestCaseService;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/10 16:57
 */
@Slf4j
@Order(10)
@Component
public class FetchReplayAndRecord implements SaveRepeatComponent {

    @Resource
    ReplayDao replayDao;

    @Resource
    RecordDao recordDao;

    @Resource
    TestCaseService testCaseService;

    @Override
    public void execute(SaveRepeatRequest saveRepeatRequest) throws BizException {
        RepeatModel rm = saveRepeatRequest.getRepeatModel();

        Replay replay = replayDao.findByRepeatId(rm.getRepeatId());
        replay.setExtend(rm.getExtension());

        Record record;
        if (replay.getType() == ReplayType.RECORD.type) {
            record = recordDao.selectById(replay.getRecordId());
        } else {
            record = testCaseService.queryTestCaseRecordById(replay.getRecordId());
        }

        replay.setStatus(rm.isFinish() ? ReplayStatus.FINISH.getStatus() : ReplayStatus.FAILED.getStatus());
        replay.setTraceId(rm.getTraceId());
        replay.setCost(rm.getCost());

        saveRepeatRequest.setReplay(replay);
        saveRepeatRequest.setRecord(record);
    }

    @Override
    public boolean isSupport(SaveRepeatRequest saveRepeatRequest) {
        return true;
    }
}
