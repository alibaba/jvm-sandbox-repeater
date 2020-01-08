package com.alibaba.repeater.console.dal.repository;

import com.alibaba.repeater.console.common.exception.BizException;
import com.alibaba.repeater.console.dal.model.Replay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
@Repository
@Transactional(rollbackFor = {RuntimeException.class, Error.class, BizException.class})
public interface ReplayRepository extends JpaRepository<Replay, Long>, JpaSpecificationExecutor<Replay> {

    Replay findByRepeatId(String repeatId);
}
