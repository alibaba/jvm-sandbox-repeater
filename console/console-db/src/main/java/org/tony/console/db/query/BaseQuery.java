package org.tony.console.db.query;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseQuery {

    private Integer page = -1;

    private Integer pageSize = 10;

    private String orderBy;

    public Map<String, Object> toParams() {
        Map<String, Object> params = new HashMap<>();

        if (page!=-1 && page!=null) {
            params.put("offset", (page-1)*pageSize);
            params.put("pageSize", pageSize);
        }

        parseParams(params);

        return params;
    }

    public abstract void parseParams(Map<String, Object> params);
}
