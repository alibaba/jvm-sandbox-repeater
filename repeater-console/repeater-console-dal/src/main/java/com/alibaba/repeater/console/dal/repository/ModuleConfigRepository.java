package com.alibaba.repeater.console.dal.repository;

import com.alibaba.repeater.console.common.exception.BizException;
import com.alibaba.repeater.console.dal.model.ModuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link ModuleConfigRepository}
 * <p>
 *
 * @author zhaoyb1990
 */
@Repository
@Transactional(rollbackFor = {RuntimeException.class, Error.class, BizException.class})
public interface ModuleConfigRepository extends JpaRepository<ModuleConfig, Long>, JpaSpecificationExecutor<ModuleConfig> {

    ModuleConfig findByAppNameAndEnvironment(String appName, String environment);
}
