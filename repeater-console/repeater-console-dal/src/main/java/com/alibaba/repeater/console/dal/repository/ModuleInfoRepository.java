package com.alibaba.repeater.console.dal.repository;

import com.alibaba.repeater.console.common.exception.BizException;
import com.alibaba.repeater.console.dal.model.ModuleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@link ModuleInfoRepository}
 * <p>
 *
 * @author zhaoyb1990
 */
@Repository
@Transactional(rollbackFor = {RuntimeException.class, Error.class, BizException.class})
public interface ModuleInfoRepository extends JpaRepository<ModuleInfo, Long>, JpaSpecificationExecutor<ModuleInfo> {


    List<ModuleInfo> findByModuleConfigId(Long configId);

    @Modifying
    @Query(
            "update ModuleInfo set gmtModified =  :#{#moduleInfo.gmtModified}" +
                    " where appName =  :#{#moduleInfo.appName}" +
                    " and ip = :#{#moduleInfo.ip}"
    )
    int updateByAppNameAndIp(@Param("moduleInfo") ModuleInfo moduleInfo);


}
