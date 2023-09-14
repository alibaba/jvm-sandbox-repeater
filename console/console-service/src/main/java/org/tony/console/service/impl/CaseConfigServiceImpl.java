package org.tony.console.service.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.TestCaseConfigMapper;
import org.tony.console.db.model.TestCaseConfig;
import org.tony.console.service.CaseConfigService;
import org.tony.console.service.model.CaseConfigType;
import org.tony.console.service.model.caseConfig.CaseCompareSortConfig;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author peng.hu1
 * @Date 2023/9/5 11:04
 */
@Component
public class CaseConfigServiceImpl implements CaseConfigService {

    @Resource
    TestCaseConfigMapper testCaseConfigMapper;

    @Override
    public CaseCompareSortConfig getCompareSortConfig(String caseId) {


        TestCaseConfig testCaseConfig = testCaseConfigMapper.selectOne(caseId, CaseConfigType.SORT_CONFIG.code);
        if (testCaseConfig==null) {
            return null;
        }

        CaseCompareSortConfig config = new CaseCompareSortConfig();
        config.setCaseId(caseId);
        config.setId(testCaseConfig.getId());
        config.setVersion(testCaseConfig.getVersion());
        config.setConfig(JSON.parseObject(testCaseConfig.getConfig(), HashMap.class));

        return config;
    }

    @Override
    public void saveCompareSortConfig(CaseCompareSortConfig config) {
        TestCaseConfig testCaseConfig = testCaseConfigMapper.selectOne(config.getCaseId(), CaseConfigType.SORT_CONFIG.code);
        if (testCaseConfig ==null) {
            testCaseConfig = new TestCaseConfig();
            testCaseConfig.setConfig(JSON.toJSONString(config.getConfig()));
            testCaseConfig.setCaseId(config.getCaseId());
            testCaseConfig.setVersion(0);
            testCaseConfig.setType(CaseConfigType.SORT_CONFIG.code);
            testCaseConfigMapper.insert(testCaseConfig);
            return;
        }

        testCaseConfig.setConfig(JSON.toJSONString(config.getConfig()));
        testCaseConfigMapper.update(testCaseConfig);
    }
}
