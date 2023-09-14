package org.tony.console.biz;

import org.tony.console.db.model.Record;
import org.tony.console.common.domain.Tag;

import java.util.List;

public interface TagBizService {

    /**|
     * 计算标签
     * @param appName
     * @param traceId
     */
    public List<Tag> compute(String appName, String traceId);

    public List<Tag> compute(Record record);
}
