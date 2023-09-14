package org.tony.console.db.dao;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.Page;
import org.tony.console.db.mapper.RecordMapper;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.RecordQuery;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RecordDao {

    @Resource
    RecordMapper recordMapper;

    public Record insert(Record record) {
        if (record != null) {
            recordMapper.insertRecord(record);
        }

        return record;
    }

    public Record selectById(Long id) {
        return recordMapper.queryById(id);
    }

    public Record selectByAppNameAndTraceId(String appName, String traceId) {
        Record record = new Record();
        record.setAppName(appName);
        record.setTraceId(traceId);
        return recordMapper.queryRecordLimit1(record);
    }

    public Page<Record> selectByAppNameOrTraceId(RecordQuery recordQuery) {
        Map<String, Object> params = recordQuery.toParams();
        long total = recordMapper.count(params);

        if (total>0) {
            List<Record> records = recordMapper.queryRecord(params);
            return Page.build(records, total);
        }

        return Page.build(Collections.<Record>emptyList(), total);
    }

    public List<Record> select(RecordQuery recordQuery) {
        return recordMapper.queryRecord(recordQuery.toParams());
    }

    public int remove(List<String> traceIdList) {
        if (CollectionUtils.isEmpty(traceIdList)) {
            return 0;
        }
        return recordMapper.batchRemove(traceIdList);
    }

    public int remove(Long id) {
        return recordMapper.removeById(id);
    }

    public int updateRecordList(List<Record> records) {
        return recordMapper.batchUpdateRecord(records);
    }
}
