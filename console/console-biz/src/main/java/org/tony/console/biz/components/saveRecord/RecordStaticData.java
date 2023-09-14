package org.tony.console.biz.components.saveRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.service.AppStaticService;

import javax.annotation.Resource;

/**
 * 数据统计
 * @author peng.hu1
 * @Date 2023/2/26 10:34
 */
@Slf4j
@Order(100)
@Component
public class RecordStaticData implements SaveRecordComponent {

    @Resource
    AppStaticService appStaticService;

    @Override
    public void execute(SaveRecordRequest saveRecordRequest) throws BizException {
        Record record = saveRecordRequest.getRecord();
        try {
            appStaticService.increaseRecordNum(record.getAppName(), 1);
        } catch (Exception e) {
            log.error("system error", e);
        }

    }

    @Override
    public boolean isSupport(SaveRecordRequest saveRecordRequest) {
        return true;
    }
}
