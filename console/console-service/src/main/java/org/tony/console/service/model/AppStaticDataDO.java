package org.tony.console.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author peng.hu1
 * @Date 2023/2/25 11:14
 */
@Data
public class AppStaticDataDO implements Serializable {

    /**
     * 今日累计采集数据
     */
    private Long totalRecord;

    /**
     * 今日累计回放次数
     */
    private Long totalReplay;

    /**
     * 累计测试用例个数
     */
    private Long totalCase;

    public AppStaticDataDO() {
        totalRecord = 0L;
        totalReplay=0L;
        totalCase = 0L;
    }
}
