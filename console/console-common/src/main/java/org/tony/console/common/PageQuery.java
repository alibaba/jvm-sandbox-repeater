package org.tony.console.common;

import lombok.Data;

@Data
public class PageQuery {

    private String pageNo;

    private String pageSize;

    private String offset;
}
