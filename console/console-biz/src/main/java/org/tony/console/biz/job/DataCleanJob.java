package org.tony.console.biz.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tony.console.biz.job.clean.CleanStrategy;
import org.tony.console.service.redis.RedisUtil;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author peng.hu1
 * @Date 2022/12/30 09:38
 */
@Slf4j
@Component
public class DataCleanJob implements InitializingBean {

    @Autowired
    List<CleanStrategy> cleanStrategyList;

    @Resource
    RedisUtil redisUtil;

    ExecutorService executorService;

    final static String REDIS_PREFIX = "CLEAN-";

    @Scheduled(cron = "0/10 * * * * ?")
    public void cleanRecord() {

        log.info("began to clean data");

        CountDownLatch countDownLatch = new CountDownLatch(cleanStrategyList.size());
        for (CleanStrategy strategy : cleanStrategyList) {
            executorService.submit(()->{
                StopWatch watch = new StopWatch();
                watch.start();
                List need2CleanList = new LinkedList();
                try {
                    long startIndex = 0L;
                    Integer size = strategy.batchSize();
                    String taskName = strategy.getName();

                    if (strategy.needStoreStartIndex()) {
                        startIndex = getStartIndex(taskName);
                    }

                    List dataList = strategy.getData(startIndex, size);

                    dataList.forEach(item->{
                        if (strategy.canClean(item)) {
                            need2CleanList.add(item);
                        }
                    });

                    strategy.clean(need2CleanList);

                    if (!strategy.needStoreStartIndex() ) {
                        return;
                    }

                    if (dataList.size()<size) {
                        resetStartIndex(taskName);
                    } else {
                        addStartIndex(taskName, size);
                    }

                } catch (Exception e) {
                    log.error("system error", e);
                } finally {
                    long cost = watch.getTime(TimeUnit.MILLISECONDS);
                    watch.stop();

                    if (need2CleanList.size()>0) {
                        log.info("task[{}] success clean {} records, cost={}ms", strategy.getName(), need2CleanList.size(), cost);
                    }

                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("system error", e);
        }

        log.info("end to clean data");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newFixedThreadPool(cleanStrategyList.size());
    }


    private long getStartIndex(String name) {
        String key = REDIS_PREFIX+name;
        if (!redisUtil.exist(key)) {
            redisUtil.set(key, "0", 2*3600*24);
            return 0L;
        }

        return Long.parseLong(redisUtil.get(key));
    }

    private void resetStartIndex(String name) {
        String key = REDIS_PREFIX+name;
        if (!redisUtil.exist(key)) {
            return;
        }

        redisUtil.set(key, "0", 2*3600*24);
    }

    private void addStartIndex(String name, int plus) {
        String key = REDIS_PREFIX+name;
        long index = getStartIndex(name);
        long newIndex = index + plus;

        redisUtil.set(key, String.valueOf(newIndex));
    }
}
