package org.tony.console.service.model;

import lombok.Data;

import java.util.*;

/**
 * @author peng.hu1
 * @Date 2022/12/1 20:54
 */
@Data
public class AppCompareConfigDO {

    /**
     * 全局忽略比对的节点
     */
    private List<String> ignoreCompareNodes = new ArrayList<>();

    /**
     * 全局需要比对的节点
     */
    private List<String> subInvokeToCompare = new ArrayList<>();

    /**
     * 子调用忽略比对的节点，按需配置
     */
    private List<SubInvokeIgnoreNode> subInvokeIgnoreCompareNodes = new LinkedList<>();
}
