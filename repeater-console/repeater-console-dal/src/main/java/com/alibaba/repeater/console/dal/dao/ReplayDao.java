package com.alibaba.repeater.console.dal.dao;

import com.alibaba.repeater.console.dal.model.Replay;
import com.alibaba.repeater.console.dal.repository.ReplayRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * {@link ReplayDao}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("replayDao")
public class ReplayDao {

    @Resource
    private ReplayRepository replayRepository;

    public Replay save(Replay replay) {
        return replayRepository.save(replay);
    }

    public Replay saveAndFlush(Replay replay) {
        return replayRepository.saveAndFlush(replay);
    }

    public Replay findByRepeatId(String repeatId) {
        return replayRepository.findByRepeatId(repeatId);
    }
}
