package org.tony.console.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.enums.Status;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.mapper.TestSuitMapper;
import org.tony.console.db.model.TestSuitDO;
import org.tony.console.db.query.TestCaseQuery;
import org.tony.console.db.query.TestSuitQuery;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.TestSuitService;
import org.tony.console.service.convert.TestSuitConvert;
import org.tony.console.service.model.TestSuitDTO;
import org.tony.console.service.model.enums.TestSuitType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:28
 */
@Component
public class TestSuitServiceImpl implements TestSuitService {

    @Resource
    TestSuitConvert testSuitConvert;

    @Resource
    TestSuitMapper testSuitMapper;

    @Resource
    TestCaseService testCaseService;

    @Override
    public List<TestSuitDTO> queryAll(String appName) {

        TestSuitQuery query = new TestSuitQuery();
        query.setStatus(Status.VALID);
        query.setAppName(appName);
        List<TestSuitDO> suitDOS = testSuitMapper.select(query.toParams());

        return testSuitConvert.convert(suitDOS);
    }

    @Override
    public TestSuitDTO addTestSuit(TestSuitDTO testSuitDTO) {
        testSuitDTO.setStatus(Status.VALID);
        TestSuitDO testSuitDO = testSuitConvert.reconvert(testSuitDTO);
        testSuitMapper.insert(testSuitDO);
        testSuitDTO.setId(testSuitDO.getId());
        return testSuitDTO;
    }

    @Override
    public TestSuitDTO queryById(Long id) {


        TestSuitDO testSuitDO = testSuitMapper.selectById(id);

        return testSuitConvert.convert(testSuitDO);
    }

    @Override
    public List<TestSuitDTO> search(TestSuitQuery query) {
        if (query.getSuitIdList()!=null && query.getSuitIdList().size()==0) {
            return new ArrayList<>(0);
        }

        List<TestSuitDO> testSuitDOS = testSuitMapper.select(query.toParams());
        return testSuitConvert.convert(testSuitDOS);
    }

    @Override
    public void removeSuit(Long suitId) throws BizException {
        TestSuitDO testSuitDO = testSuitMapper.selectById(suitId);
        if (testSuitDO == null) {
            return;
        }

        if (testSuitDO.getType().equals(TestSuitType.CATALOG.code)) {
            TestSuitQuery query = new TestSuitQuery();
            query.setStatus(Status.VALID);
            query.setParentId(suitId);
            List<TestSuitDO> suitDOS = testSuitMapper.select(query.toParams());
            if  (!CollectionUtils.isEmpty(suitDOS)) {
                throw BizException.build("该目录下还有子目录/子任务，请先删除子任务");
            }
        }

        if (testSuitDO.getType().equals(TestSuitType.Task.code)) {
            TestCaseQuery caseQuery = new TestCaseQuery();
            caseQuery.setSuitIdList(Collections.singletonList(suitId));
            long caseSize = testCaseService.count(caseQuery);
            if (caseSize>0) {
                throw BizException.build("该任务底下还有未清理的case，请先删除case先");
            }
        }

        testSuitMapper.delete(suitId);
    }
}
