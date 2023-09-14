package org.tony.console.service.model;

import lombok.Data;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/31 10:47
 */
@Data
public class SubInvokeIgnoreNode {

    private String identity;

    private List<String> ignoreNodes;
}
