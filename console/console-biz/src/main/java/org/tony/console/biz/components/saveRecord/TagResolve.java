package org.tony.console.biz.components.saveRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.TagBizService;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.SaveRecordRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.mongo.model.RecordMDO;
import org.tony.console.common.domain.Tag;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/17 14:17
 */
@Slf4j
@Order(30)
@Component
public class TagResolve  implements SaveRecordComponent {

    @Resource
    TagBizService tagBizService;

    @Override
    public void execute(SaveRecordRequest saveRecordRequest) throws BizException {
        Record record = saveRecordRequest.getRecord();

        if (record == null) {
            return;
        }

        RecordMDO recordMDO = convert(record, saveRecordRequest.getTagList());
        saveRecordRequest.setRecordMDO(recordMDO);

        try {
            List<Tag> tags = tagBizService.compute(record);
            if (!CollectionUtils.isEmpty(tags)) {
                recordMDO.setTags(tags);
            }
        } catch (Exception e) {
            log.error("system error", e);
        }
    }

    @Override
    public boolean isSupport(SaveRecordRequest saveRecordRequest) {
        return true;
    }

    private RecordMDO convert(Record record, List<Tag> tags) {
        RecordMDO recordMDO = new RecordMDO();
        recordMDO.setGmtRecord(record.getGmtRecord());
        recordMDO.setEnv(record.getEnvironment());
        recordMDO.setAppName(record.getAppName());
        recordMDO.setEntranceDesc(record.getEntranceDesc());
        recordMDO.setHost(record.getHost());
        recordMDO.setTraceId(record.getTraceId());
        recordMDO.setTags(tags);
        recordMDO.setVersion(record.getVersion());

        recordMDO.setRecordType(record.getType());

        return recordMDO;
    }
}
