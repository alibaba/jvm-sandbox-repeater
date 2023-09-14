package org.tony.console.db.dao;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.ReplayMapper;
import org.tony.console.db.model.Replay;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReplayDao {

    @Resource
    ReplayMapper replayMapper;

    public Replay save(Replay replay) {
        replayMapper.insertReplay(replay);
        return replay;
    }

    public Replay saveAndFlush(Replay replay) {
        replayMapper.updateReplay(replay);
        return replay;
    }

    public Replay findByRepeatId(String repeatId) {
        Map<String, Object> query = ImmutableMap.<String, Object> builder()
                .put("repeatId", repeatId)
                .build();
        return replayMapper.queryReplayLimit1(query);
    }
}
