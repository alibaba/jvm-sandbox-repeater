package org.tony.console.service.model.caseConfig;

import lombok.Data;

import java.util.Map;

/**
 * 比对排序配置
 * @author peng.hu1
 * @Date 2023/9/5 10:46
 */
@Data
public class CaseCompareSortConfig {

    private Long id;

    private String caseId;

    private Map<String, String> config;

    private int version;
}
