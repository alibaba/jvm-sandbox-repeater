package com.alibaba.repeater.console.dal.dao;

import com.alibaba.repeater.console.common.params.RecordParams;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.dal.repository.RecordRepository;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * {@link RecordDao}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("recordDao")
public class RecordDao {

    @Resource
    private RecordRepository recordRepository;

    public Record insert(Record record) {
        return recordRepository.save(record);
    }

    public Record selectByAppNameAndTraceId(String appName, String traceId) {
        return recordRepository.findByAppNameAndTraceId(appName, traceId);
    }

    public Page<Record> selectByAppNameOrTraceId(@NotNull final RecordParams params) {
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getSize(), new Sort(Sort.Direction.DESC,"id"));
        return recordRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = Lists.newArrayList();
                    if (params.getAppName() != null && !params.getAppName().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("appName"), params.getAppName()));
                    }
                    if (params.getTraceId() != null && !params.getTraceId().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("traceId"), params.getTraceId()));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );
    }
}
