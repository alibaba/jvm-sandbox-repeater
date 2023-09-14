package org.tony.console.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.service.AppStaticService;
import org.tony.console.service.model.AppStaticDataDO;
import org.tony.console.service.redis.RedisUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/2/25 11:33
 */
@Component
public class AppStaticServiceImpl implements AppStaticService {

    @Resource
    RedisUtil redisUtil;

    @Value("${app.static.expire.day}")
    private long expireTime = 7;

    private final static String KEY_PREFIX = "repeater.static.";

    @Override
    public void increaseRecordNum(String appName, int delta) {
        Date date = new Date();
        checkExist(appName, date);
        redisUtil.hIncrement(getKey(appName, date), "totalRecord", delta);
    }

    @Override
    public void increaseReplayNum(String appName, int delta) {
        Date date = new Date();
        checkExist(appName, date);
        redisUtil.hIncrement(getKey(appName, date), "totalReplay", delta);
    }

    @Override
    public void increaseCaseNum(String appName, int delta) {

    }

    @Override
    public AppStaticDataDO queryStaticDataOfNow(String appName) {
        Date date = new Date();
        String key = checkExist(appName, date);

        return (AppStaticDataDO) redisUtil.hgetAllT(key);
    }

    @Override
    public AppStaticDataDO queryStaticDataOfDate(String appName, Date date) {
        String key = checkExist(appName, date);
        AppStaticDataDO res=  (AppStaticDataDO) redisUtil.hgetAllT(key);
        if (res == null) {
            return new AppStaticDataDO();
        }

        return res;
    }

    private String checkExist(String appName, Date date) {
        String key = getKey(appName, date);
        if (!redisUtil.exist(key)) {
            initRedis(appName, date);
        }

        return key;
    }

    private void initRedis(String appName,  Date date) {
        String key = getKey(appName, date);

        AppStaticDataDO appStaticDataDO = new AppStaticDataDO();
        appStaticDataDO.setTotalRecord(0L);
        appStaticDataDO.setTotalCase(0L);
        appStaticDataDO.setTotalReplay(0L);

        redisUtil.hPutAllT(key, appStaticDataDO);

        //几天过期
        redisUtil.expire(key, expireTime*3600*24);
    }

    private String getKey(String appName, Date date) {
        return String.format("%s.%s.%s", KEY_PREFIX, appName, DateUtil.getDateTime(date));
    }
}
