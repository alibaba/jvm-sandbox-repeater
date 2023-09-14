package org.tony.console.service;

import org.tony.console.service.model.AppStaticDataDO;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/2/25 11:29
 */
public interface AppStaticService {

    /**
     * 计数， 新增录制数据大小
     * @param appName
     * @param delta
     */
    public void increaseRecordNum(String appName, int delta);

    /**
     * 计数回放次数
     * @param appName
     * @param delta
     */
    public void increaseReplayNum(String appName, int delta);

    /**
     * 计数用例个数
     * @param appName
     * @param delta
     */
    public void increaseCaseNum(String appName, int delta);

    /**
     * 查询应用当天的统计数据
     * @param appName
     * @return
     */
    public AppStaticDataDO queryStaticDataOfNow(String appName);

    /**
     * 获取指定时间的统计数据
     * @param appName
     * @param date
     * @return
     */
    public AppStaticDataDO queryStaticDataOfDate(String appName, Date date);
}
