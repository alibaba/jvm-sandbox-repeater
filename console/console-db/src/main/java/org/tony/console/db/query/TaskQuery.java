package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.tony.console.common.enums.TaskStatus;

import java.util.Date;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/1/5 14:30
 */
@Data
public class TaskQuery extends BaseQuery {

    TaskStatus taskStatus;

    String appName;

    Date gmtStartLt;

    Boolean orderByGmtCreateDesc;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (taskStatus!=null) {
            params.put("status", taskStatus.code);
        }

        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }

        if (gmtStartLt!=null) {
            params.put("gmtStartLt", gmtStartLt);
        }

        if (orderByGmtCreateDesc!=null && orderByGmtCreateDesc) {
            params.put("orderByGmtCreateDesc", 1);
        }
    }
}
