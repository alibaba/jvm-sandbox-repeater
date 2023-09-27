package org.tony.console.biz.job.clean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.mapper.ReplayMapper;
import org.tony.console.db.model.Replay;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/4/8 11:23
 */
@Component
public class ReplayCleanStrategy implements CleanStrategy<Replay> {

    @Value("${replay.idle.day}")
    private Integer replayIdleDay = 30;

    @Value("${replay.idle.clean.bath.size}")
    private Integer batchSize = 100;

    @Resource
    ReplayMapper replayMapper;

    @Override
    public List<Replay> getData(long startIndex, Integer size) {
        return replayMapper.queryReplayWithSize(size);
    }

    @Override
    public boolean needStoreStartIndex() {
        return false;
    }

    @Override
    public Integer batchSize() {
        return batchSize;
    }

    @Override
    public boolean canClean(Replay item) {

        if (DateUtil.getDateGapDay(item.getGmtCreate(), new Date())>replayIdleDay) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(List<Replay> itemList) {
        for (Replay item : itemList) {
            replayMapper.delete(item);
        }
    }

    @Override
    public String getName() {
        return "REPLAY-CLEAN-TASK";
    }
}
