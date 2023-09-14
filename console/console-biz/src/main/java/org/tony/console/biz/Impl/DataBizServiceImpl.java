package org.tony.console.biz.Impl;

import org.springframework.stereotype.Component;
import org.tony.console.biz.DataBizService;
import org.tony.console.biz.model.chart.ContentDataRecord;
import org.tony.console.biz.model.chart.PanelData;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.service.AppService;
import org.tony.console.service.AppStaticService;
import org.tony.console.service.model.AppStaticDataDO;
import org.tony.console.service.model.app.AppDTO;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/26 11:10
 */
@Component
public class DataBizServiceImpl implements DataBizService {

    @Resource
    AppStaticService appStaticService;

    @Resource
    AppService appService;

    @Override
    public List<ContentDataRecord> queryRecordChart(String appName) {

        List<ContentDataRecord> resList = new LinkedList<>();

        //查询最近7天的数据
        Date now = DateUtil.getDay(new Date(), -7);
        AppStaticDataDO appStaticDataDO = appStaticService.queryStaticDataOfDate(appName, now);
        resList.add(convert(now, appStaticDataDO));

        for (int i=1; i<8; i++) {
            Date d = DateUtil.getDay(now, i);
            resList.add(convert(
                    d,
                    appStaticService.queryStaticDataOfDate(appName, d)
            ));
        }

        return resList;
    }

    @Override
    public List<ContentDataRecord> queryBuRecordChart(String appName) {

        AppDTO appDTO = appService.queryApp(appName);

        List<AppDTO> appDTOS = appService.queryAppGroup(appDTO.getBuId());

        List<ContentDataRecord> recordList = new LinkedList<>();

        for (AppDTO item : appDTOS) {
            ContentDataRecord r = new ContentDataRecord();
            r.setX(item.getName());
            r.setY(appStaticService.queryStaticDataOfNow(item.getName()).getTotalRecord());

            recordList.add(r);
        }

        return recordList;
    }

    @Override
    public PanelData queryPanelData(String appName) {
        Date now = new Date();
        Date yest = DateUtil.getDay(now, -1);

        AppStaticDataDO nowData = appStaticService.queryStaticDataOfDate(appName, now);
        AppStaticDataDO yestData = appStaticService.queryStaticDataOfDate(appName, yest);

        PanelData panelData = new PanelData();
        panelData.setTotalRecord(nowData.getTotalRecord());
        panelData.setTotalRecordYest(yestData.getTotalRecord());
        panelData.setTotalRepeat(nowData.getTotalReplay());

        double plus = 0;
        if (yestData.getTotalRecord()==0) {
            plus = 100;
        } else {
            plus = (panelData.getTotalRecord() - panelData.getTotalRecordYest()) *1.0/ panelData.getTotalRecordYest();
        }
        panelData.setPlus(plus);
        return panelData;
    }

    private ContentDataRecord convert(Date date, AppStaticDataDO appStaticDataDO) {
        ContentDataRecord contentDataRecord = new ContentDataRecord();
        contentDataRecord.setX(DateUtil.getDateTime(date));

        if (appStaticDataDO !=null) {
            contentDataRecord.setY(appStaticDataDO.getTotalRecord());
        } else {
            contentDataRecord.setY(0);
        }

        return contentDataRecord;
    }
}
