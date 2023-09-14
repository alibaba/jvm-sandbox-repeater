package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.enums.Status;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:55
 */
@Data
public class TestSuitQuery extends  BaseQuery {

    private Status status;

    private String appName;

    private String name;

    private Integer type;

    private Long parentId;

    private List<Long> suitIdList;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (status!=null) {
            params.put("status", status.code);
        }

        if (appName!=null) {
            params.put("appName", appName);
        }

        if (StringUtils.isNotEmpty(name)) {
            params.put("name", name);
        }

        if (type!=null) {
            params.put("type", type);
        }

        if (parentId!=null) {
            params.put("parentId", parentId);
        }

        if (!CollectionUtils.isEmpty(suitIdList)) {
            params.put("suitIdList", suitIdList);
        }
    }
}
