package org.tony.console.db.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:34
 */
@Data
public class TagConfigQuery extends BaseQuery {

    private String appName;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appName", appName);
        }
    }
}
