package org.tony.console.mongo;

import org.tony.console.common.domain.PageResult;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.model.RecordMDO;

import java.util.List;

public interface RecordMongoService {

    public void insert(RecordMDO record);

    public void remove(String traceId);

    public PageResult<RecordMDO> queryRecord(RecordQuery recordQuery);

    public List<RecordMDO> queryByIdList(List<String> idList);
}
