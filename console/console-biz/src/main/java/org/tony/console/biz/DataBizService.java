package org.tony.console.biz;

import org.tony.console.biz.model.chart.ContentDataRecord;
import org.tony.console.biz.model.chart.PanelData;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/26 11:07
 */
public interface DataBizService {

    public List<ContentDataRecord> queryRecordChart(String appName);

    /**
     * 查询这个应用归属bu的今日采集数据
     * @param appName
     * @return
     */
    public List<ContentDataRecord> queryBuRecordChart(String appName);

    public PanelData queryPanelData(String appName);
}
