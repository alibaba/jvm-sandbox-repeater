package org.tony.console.biz.components.saveRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.service.utils.ConvertUtil;


/**
 * @author peng.hu1
 * @Date 2022/12/18 14:24
 */
@Slf4j
@Order(5)
@Component
public class RecordCompose implements SaveRecordComponent {

    @Override
    public void execute(SaveRecordRequest saveRecordRequest) throws BizException {
        Record record = ConvertUtil.convertWrapper(saveRecordRequest.getRecordWrapper(), saveRecordRequest.getBody());
        saveRecordRequest.setRecord(record);
    }

    @Override
    public boolean isSupport(SaveRecordRequest saveRecordRequest) {
        return true;
    }
}
