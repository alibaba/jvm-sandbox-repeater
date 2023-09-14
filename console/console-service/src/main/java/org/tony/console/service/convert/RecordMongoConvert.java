package org.tony.console.service.convert;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.domain.RecordBO;
import org.tony.console.common.domain.RecordType;
import org.tony.console.mongo.model.RecordMDO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/17 15:14
 */
@Component
public class RecordMongoConvert {

    public List<RecordBO> convert(List<RecordMDO> recordMDOList) {
        if (CollectionUtils.isEmpty(recordMDOList)) {
            return new ArrayList<>(0);
        }

        return recordMDOList.stream().map(this::convert).collect(Collectors.toList());
    }


    public RecordBO convert(RecordMDO mdo) {
        RecordBO recordBO = new RecordBO();
        recordBO.setId(mdo.getId());
        recordBO.setGmtRecord(mdo.getGmtRecord());
        recordBO.setTraceId(mdo.getTraceId());
        recordBO.setHost(mdo.getHost());
        recordBO.setEntranceDesc(mdo.getEntranceDesc());
        if (mdo.getAddCase()!=null) {
            recordBO.setAdded(mdo.getAddCase());
        } else {
            recordBO.setAdded(false);
        }
        recordBO.setGmtCreate(mdo.getGmtRecord());
        recordBO.setEnvironment(mdo.getEnv());
        recordBO.setAppName(mdo.getAppName());
        recordBO.setVersion(mdo.getVersion());

        if (mdo.getRecordType()!=null) {
            recordBO.setRecordType(RecordType.getByString(mdo.getRecordType()));
        } else {
            if (recordBO.getEntranceDesc().startsWith("java")){
                recordBO.setRecordType(RecordType.JAVA);
            } else {
                recordBO.setRecordType(RecordType.HTTP);
            }
        }

        recordBO.setTags(mdo.getTags());

        return recordBO;
    }
}
