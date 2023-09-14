package org.tony.console.service;

import org.tony.console.service.model.caseConfig.CaseCompareSortConfig;

/**
 * @author peng.hu1
 * @Date 2023/9/5 10:45
 */
public interface CaseConfigService {

    /**
     * 获取排序配置
     * @param caseId 用例id
     * @return
     */
    public CaseCompareSortConfig getCompareSortConfig(String caseId);

    /**
     * 保存排序配置
     * @param config 配置
     */
    public void saveCompareSortConfig(CaseCompareSortConfig config);


}
