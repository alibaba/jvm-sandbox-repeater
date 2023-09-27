package org.tony.console.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.RecordMongoService;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/7/17 13:01
 */
@Slf4j
@Component
public class DataCleanService {

    @Resource
    RecordDao recordDao;

    @Resource
    RecordMongoService recordMongoService;

    ExecutorService executorService2 = Executors.newFixedThreadPool(2);

    public void cleanData(String appName, int size) throws ExecutionException, InterruptedException {


        int pageSize=500;
        int page = 1;

        log.info("============== began clean ==============");
        int total = 0;
        while (page*pageSize<=size) {
            page++;
            List<Record> itemList = getData(appName, pageSize);

            int s = clean(itemList);
            total = total + s;

            log.info("success clean i={}", total);
        }
        log.info("============== end clean ==============");
    }

    private int clean(List<Record> itemList) throws ExecutionException, InterruptedException {
        List<String> traceIdList = itemList.stream().map(Record::getTraceId).collect(Collectors.toList());
        Future<String> s1 = executorService2.submit(()->{
            int i=1;
            for (Record record : itemList) {
                recordDao.remove(record.getId());
                i++;
            }

            return "success";
        });

        Future<String> s2 = executorService2.submit(()->{
            int j=1;
            for (String traceId : traceIdList) {
                recordMongoService.remove(traceId);
                j++;
            }

            return "success";
        });

        s1.get();
        s2.get();

        return itemList.size();
    }

    private List<Record> getData(String appName, Integer size) {

        RecordQuery recordQuery = new RecordQuery();

        recordQuery.setPage(1);
        recordQuery.setPageSize(size);
        recordQuery.setAppName(appName);

        return recordDao.select(recordQuery);
    }
}
