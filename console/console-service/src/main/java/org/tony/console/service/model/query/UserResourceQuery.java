package org.tony.console.service.model.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.tony.console.common.enums.Status;
import org.tony.console.db.query.BaseQuery;
import org.tony.console.service.model.enums.ResourceType;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:35
 */
@Data
public class UserResourceQuery extends BaseQuery {

    private Long rid;

    private String user;

    private Status status;

    private String name;

    private ResourceType type;

    @Override
    public void parseParams(Map<String, Object> params) {
        if (rid!=null) {
            params.put("rid", rid);
        }

        if (user!=null) {
            params.put("user", user);
        }

        if (status!=null) {
            params.put("status", status.code);
        }

        if (StringUtils.isNotEmpty(name)) {
            params.put("name", name);
        }

        if (type!=null) {
            params.put("type", type.code);
        }
    }
}
