package org.tony.console.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.mapper.TestCaseRecordMapper;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.TestCaseDO;
import org.tony.console.db.model.TestCaseRecordDO;
import org.tony.console.db.sequence.SequenceException;
import org.tony.console.db.sequence.TestCaseSequence;
import org.tony.console.service.convert.TestCaseConvert;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.db.mapper.TestCaseMapper;
import org.tony.console.db.query.TestCaseQuery;
import org.tony.console.service.TestCaseService;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/14 14:21
 */
@Component
public class TestCaseServiceImpl implements TestCaseService {

    @Resource
    TestCaseMapper testCaseMapper;

    @Resource
    TestCaseSequence testCaseSequence;

    @Resource
    TestCaseRecordMapper testCaseRecordMapper;

    @Resource
    RecordDao recordDao;

    @Resource
    TestCaseConvert testCaseConvert;


    @Override
    public void addTestCase(List<TestCaseDTO> testCaseBOList) throws BizException {
        try {
            List<Long> idList = testCaseSequence.nextVal(testCaseBOList.size());
            List<TestCaseRecordDO> testCaseRecordDOList = new LinkedList<>();
            for (int i=0; i<testCaseBOList.size(); i++) {
                TestCaseDTO testCaseDTO = testCaseBOList.get(i);
                String caseId = buildCaseId(idList.get(i));
                testCaseDTO.setCaseId(caseId);

                TestCaseRecordDO testCaseRecordDO = copy(testCaseDTO.getRecord());
                testCaseRecordDO.setCaseId(caseId);
                testCaseRecordDOList.add(testCaseRecordDO);
            }

            testCaseMapper.insertCaseList(testCaseConvert.reconvertList(testCaseBOList));
            testCaseRecordMapper.insert(testCaseRecordDOList);
        } catch (SequenceException e) {
            e.printStackTrace();
        }
    }

    private String buildCaseId(long id) {
        return "TEST"+ id;
    }

    private TestCaseRecordDO copy(Record record) {
        TestCaseRecordDO testCaseRecordDO = new TestCaseRecordDO();
        testCaseRecordDO.setWrapperRecord(record.getWrapperRecord());
        testCaseRecordDO.setTraceId(record.getTraceId());
        testCaseRecordDO.setResponse(record.getResponse());
        testCaseRecordDO.setHost(record.getHost());
        testCaseRecordDO.setGmtCreate(record.getGmtCreate());
        testCaseRecordDO.setEnvironment(record.getEnvironment());
        testCaseRecordDO.setEntranceDesc(record.getEntranceDesc());
        testCaseRecordDO.setAppName(record.getAppName());
        testCaseRecordDO.setRequest(record.getRequest());
        testCaseRecordDO.setGmtRecord(record.getGmtRecord());
        testCaseRecordDO.setType(record.getType());

        return testCaseRecordDO;
    }

    @Override
    public PageResult<TestCaseDTO> queryTestCaseWithPage(TestCaseQuery query) {
        Map<String, Object> params = query.toParams();
        params.put("orderByGmtCreateDesc", 1);
        params.put("delete", 0);
        Long size = testCaseMapper.count(params);
        List<TestCaseDO> testCaseDOList = testCaseMapper.selectTestCaseList(params);

        return PageResult.buildSuccess(testCaseConvert.convert(testCaseDOList), size);
    }

    @Override
    public List<TestCaseDTO> queryTestCaseList(TestCaseQuery testCaseQuery) {

        List<TestCaseDO> testCaseDOList = testCaseMapper.selectTestCaseList(testCaseQuery.toParams());
        return testCaseConvert.convert(testCaseDOList);
    }

    @Override
    public TestCaseDTO queryTestCaseDTO(String caseId) {
        TestCaseDO testCaseDO = testCaseMapper.selectTestCaseById(caseId);
        TestCaseDTO testCaseDTO =  testCaseConvert.convert(testCaseDO);
        if (testCaseDTO == null) {
            return null;
        }

        TestCaseRecordDO testCaseRecordDO = testCaseRecordMapper.selectByCaseId(caseId);
        if (testCaseRecordDO!=null) {
            testCaseDTO.setRecord(convert(testCaseRecordDO));
        }

        return testCaseDTO;
    }

    @Override
    public Record queryTestCaseRecord(String caseId) {
        TestCaseRecordDO testCaseRecordDO = testCaseRecordMapper.selectByCaseId(caseId);
        if (testCaseRecordDO!=null) {
            return convert(testCaseRecordDO);
        }
        return null;
    }

    @Override
    public int updateRecord(String caseId, Record record) {
        TestCaseRecordDO testCaseRecordDO = new TestCaseRecordDO();
        testCaseRecordDO.setCaseId(caseId);
        testCaseRecordDO.setResponse(record.getResponse());
        testCaseRecordDO.setWrapperRecord(record.getWrapperRecord());

        return testCaseRecordMapper.update(testCaseRecordDO);
    }

    @Override
    public Record queryTestCaseRecordById(Long id) {
        TestCaseRecordDO testCaseRecordDO = testCaseRecordMapper.selectById(id);
        if (testCaseRecordDO!=null) {
            return convert(testCaseRecordDO);
        }
        return null;
    }

    private Record convert(TestCaseRecordDO testCaseRecordDO) {
        Record record = new Record();
        record.setId(testCaseRecordDO.getId());
        record.setResponse(testCaseRecordDO.getResponse());
        record.setWrapperRecord(testCaseRecordDO.getWrapperRecord());
        record.setHost(testCaseRecordDO.getHost());
        record.setEnvironment(testCaseRecordDO.getEnvironment());
        record.setRequest(testCaseRecordDO.getRequest());
        record.setTraceId(testCaseRecordDO.getTraceId());
        record.setAppName(testCaseRecordDO.getAppName());
        record.setEntranceDesc(testCaseRecordDO.getEntranceDesc());

        return record;
    }

    @Override
    public long count(TestCaseQuery query) {
        return testCaseMapper.count(query.toParams());
    }

    @Override
    public void removeTestCase(List<String> caseIdList) {
        testCaseMapper.removeCaseList(caseIdList);
        testCaseRecordMapper.remove(caseIdList);
    }

    @Override
    public void changeTestCaseSuit(List<String> caseIdList, Long suitId) {
        if (CollectionUtils.isEmpty(caseIdList)) {
            return;
        }

        TestCaseQuery query = new TestCaseQuery();
        query.setCaseIdList(caseIdList);

        List<TestCaseDO> testCaseDOList = testCaseMapper.selectTestCaseList(query.toParams());
        testCaseDOList.forEach(item->item.setSuitId(suitId));
        testCaseMapper.updateCaseList(testCaseDOList);
    }

    @Override
    public void removeTestCaseOfSuit(Long suitId) {
        testCaseMapper.removeTestCaseOfSuit(suitId);
    }
}
