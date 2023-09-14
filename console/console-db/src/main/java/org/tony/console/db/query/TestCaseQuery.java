package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/14 14:15
 */
@Data
public class TestCaseQuery extends  BaseQuery {

    private List<Long> suitIdList;

    private List<String> caseIdList;

    private String appName;

    private String caseId;

    private String caseName;

    private boolean delete = false;

    private String entrance;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }

        if (StringUtils.isNotEmpty(caseName)) {
            params.put("caseName", caseName);
        }

        if (StringUtils.isNotEmpty(caseId)) {
            params.put("caseId", caseId);
        }

        if (StringUtils.isNotEmpty(entrance)) {
            params.put("entrance", entrance);
        }

        if (!CollectionUtils.isEmpty(suitIdList)) {
            params.put("suitIdList", suitIdList);
        }

        if (!CollectionUtils.isEmpty(caseIdList)) {
            params.put("caseIdList", caseIdList);
        }

        if (delete) {
            params.put("delete", 1);
        } else {
            params.put("delete", 0);
        }
    }
}
