package org.tony.console.biz.components.saveRecord;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.mongo.RecordMongoService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/18 15:03
 */
@Slf4j
@Order(50)
@Component
public class SaveToDB implements SaveRecordComponent {

    @Resource
    RecordDao recordDao;

    @Resource
    RecordMongoService recordMongoService;

    @Override
    public void execute(SaveRecordRequest saveRecordRequest) throws BizException {
        Record record = saveRecordRequest.getRecord();

        if (record == null) {
            return;
        }

        if (StringUtils.isNotBlank(record.getEntranceDesc())) {
            if (record.getEntranceDesc().length()>1000) {
                record.setEntranceDesc(record.getEntranceDesc().substring(0, 1000));
            }
        }

        recordDao.insert(record);
        record.setGmtCreate(new Date());
        //同步插入mongo
        recordMongoService.insert(saveRecordRequest.getRecordMDO());
    }

    @Override
    public boolean isSupport(SaveRecordRequest saveRecordRequest) {
        return true;
    }
}
