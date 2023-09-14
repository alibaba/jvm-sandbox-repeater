package org.tony.console.biz.Impl;

import org.springframework.stereotype.Component;
import org.tony.console.biz.RecordBizService;
import org.tony.console.biz.components.BizFactory;
import org.tony.console.biz.components.BizSession;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.addTestCase.AddTestCaseBizComponent;
import org.tony.console.biz.components.saveRecord.SaveRecordComponent;
import org.tony.console.biz.components.saveRepeat.SaveRepeatComponent;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.biz.request.SaveRepeatRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.service.convert.RecordDetailConverter;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:55
 */
@Component
public class RecordBizServiceImpl implements RecordBizService {

    @Resource
    BizFactory bizFactory;

    @Resource
    RecordDao recordDao;

    @Resource
    RecordDetailConverter recordDetailConverter;

    @Override
    public void saveRecord(String body) throws BizException {

        SaveRecordRequest saveRecordRequest = new SaveRecordRequest();
        saveRecordRequest.setBody(body);

        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(SaveRecordComponent.class, saveRecordRequest);
            }

        }.execute();
    }

    @Override
    public void saveRepeat(String body) throws BizException {
        SaveRepeatRequest saveRepeatRequest = new SaveRepeatRequest();
        saveRepeatRequest.setBody(body);

        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(SaveRepeatComponent.class, saveRepeatRequest);
            }

        }.execute();
    }

    @Override
    public RecordDetailBO get(String appName, String traceId) {
        Record record = recordDao.selectByAppNameAndTraceId(appName, traceId);
        if (record == null) {
            return null;
        }
        return recordDetailConverter.convert(record);
    }

    @Override
    public void saveRecord(String msgKey, String msgBody) {

    }
}
