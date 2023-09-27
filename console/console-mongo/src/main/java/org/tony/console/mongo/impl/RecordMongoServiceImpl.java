package org.tony.console.mongo.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.PageResult;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.RecordMongoService;
import org.tony.console.mongo.model.RecordMDO;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/30 15:15
 */
@Component
public class RecordMongoServiceImpl implements RecordMongoService {

    private final static String Collection_Record = "record";

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void insert(RecordMDO record) {

        mongoTemplate.insert(record, Collection_Record);
    }

    @Override
    public void remove(String traceId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("traceId").is(traceId);
        query.addCriteria(criteria);
        mongoTemplate.findAllAndRemove(query, Collection_Record);
    }

    @Override
    public PageResult<RecordMDO> queryRecord(RecordQuery recordQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (StringUtils.isNotEmpty(recordQuery.getAppName())) {
            criteria.and("appName").is(recordQuery.getAppName());
        }

        if (StringUtils.isNotEmpty(recordQuery.getTraceId())) {
            criteria.and("traceId").is(recordQuery.getTraceId());
        }

        if (StringUtils.isNotEmpty(recordQuery.getEntranceDesc())) {
            criteria.and("entranceDesc").regex(recordQuery.getEntranceDesc());
        }

        if (StringUtils.isNotEmpty(recordQuery.getEnvironment())) {
            criteria.and("env").is(recordQuery.getEnvironment().trim().toLowerCase());
        }

        if (StringUtils.isNotEmpty(recordQuery.getIp())) {
            criteria.and("host").is(recordQuery.getIp().trim());
        }

        query.addCriteria(criteria);
        long total = mongoTemplate.count(query, Collection_Record);

        int page = recordQuery.getPage();
        int pageSize = recordQuery.getPageSize();

        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC,"gmtRecord"));
        query.with(pageable);
        List<RecordMDO> recordMDOList = mongoTemplate.find(query, RecordMDO.class, Collection_Record);
        return PageResult.buildSuccess(recordMDOList, total);
    }

    @Override
    public List<RecordMDO> queryByIdList(List<String> idList) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").in(idList);
        query.addCriteria(criteria);

        List<RecordMDO> recordMDOList = mongoTemplate.find(query, RecordMDO.class, Collection_Record);
        return recordMDOList;
    }
}
