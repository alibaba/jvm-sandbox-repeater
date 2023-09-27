package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class RecordQuery extends  BaseQuery {

    private String appName;

    private String traceId;

    private String requestId;

    private String entranceDesc;

    private String environment;

    private List<Long> recordIdList;

    private List<String> traceIdList;

    /**
     * 创建时间小于该时间
     */
    private Date gmtCreateLt;

    private Boolean orderByGmtCreateDesc;

    private String ip;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }

        if (StringUtils.isNotEmpty(traceId)) {
            params.put("traceId", traceId);
        }

        if (StringUtils.isNotEmpty(requestId)) {
            params.put("requestId", requestId);
        }

        if (StringUtils.isNotEmpty(entranceDesc)) {
            params.put("entranceDesc", entranceDesc);
        }

        if (StringUtils.isNotEmpty(environment)) {
            params.put("environment", environment);
        }

        if (!CollectionUtils.isEmpty(recordIdList)) {
            params.put("recordIdList", recordIdList);
        }

        if (!CollectionUtils.isEmpty(traceIdList)) {
            params.put("traceIdList", traceIdList);
        }

        if (gmtCreateLt!=null) {
            params.put("gmtCreateLt", gmtCreateLt);
        }

        if (orderByGmtCreateDesc!=null && orderByGmtCreateDesc) {
            params.put("orderByGmtCreateDesc", 1);
        }
    }
}
