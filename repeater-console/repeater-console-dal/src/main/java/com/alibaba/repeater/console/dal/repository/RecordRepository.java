package com.alibaba.repeater.console.dal.repository;

        import com.alibaba.repeater.console.common.exception.BizException;
        import com.alibaba.repeater.console.dal.model.Record;
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
public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    /**
     * 根据应用名和traceId索引
     *
     * @param appName 应用名
     * @param traceId traceId
     * @return 录制记录
     */
    Record findByAppNameAndTraceId(String appName, String traceId);
}
