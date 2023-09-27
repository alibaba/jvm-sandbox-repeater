package org.tony.console.biz.job.clean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.RecordMongoService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/4/8 10:51
 */
@Component
public class RecordCleanStrategy implements CleanStrategy<Record>{

    @Value("${record.idle.day}")
    private Integer recordIdleDay = 5;

    @Value("${record.idle.clean.bath.size}")
    private Integer batchSize = 100;

    @Resource
    private RecordDao recordDao;

    @Resource
    RecordMongoService recordMongoService;

    @Override
    public List<Record> getData(long startIndex, Integer size) {

        RecordQuery recordQuery = new RecordQuery();

        recordQuery.setPage(1);
        recordQuery.setPageSize(size);

        return recordDao.select(recordQuery);
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
    public boolean canClean(Record item) {
        if (DateUtil.getDateGapDay(item.getGmtCreate(), new Date())>recordIdleDay) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(List<Record> itemList) {
        List<String> traceIdList = itemList.stream().map(Record::getTraceId).collect(Collectors.toList());

        for (Record record : itemList) {
            recordDao.remove(record.getId());
        }

        for (String traceId : traceIdList) {
            recordMongoService.remove(traceId);
        }
    }

    @Override
    public String getName() {
        return "RECORD-CLEAN-TASK";
    }
}
