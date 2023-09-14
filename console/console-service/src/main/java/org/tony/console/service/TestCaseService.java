package org.tony.console.service;

import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.db.query.TestCaseQuery;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/14 14:12
 */
public interface TestCaseService {

    /**
     * 添加测试用例
     * @param testCaseBOList
     */
    public void addTestCase(List<TestCaseDTO> testCaseBOList) throws BizException;

    /**
     * 查询用例（带分页）
     * @return
     */
    public PageResult<TestCaseDTO> queryTestCaseWithPage(TestCaseQuery query);

    /**
     * 查询用例
     * @param testCaseQuery
     * @return
     */
    public List<TestCaseDTO> queryTestCaseList(TestCaseQuery testCaseQuery);

    /**
     * 根据caseId查询
     * @param caseId
     * @return
     */
    public TestCaseDTO queryTestCaseDTO(String caseId);

    /**
     * 查询用例record
     * @param caseId
     * @return
     */
    public Record queryTestCaseRecord(String caseId);

    /**
     * 更新record
     * @param caseId 用例id
     * @param record record
     * @return
     */
    public int updateRecord(String caseId, Record record);

    public Record queryTestCaseRecordById(Long id);

    /**
     * 统计
     * @param query
     * @return
     */
    public long count(TestCaseQuery query);

    /**
     * 删除case
     * @param caseIdList
     */
    public void removeTestCase(List<String> caseIdList);


    /**
     * 变更用例的任务id
     * @param caseIdList
     * @param suitId
     */
    public void changeTestCaseSuit(List<String> caseIdList, Long suitId);


    /**
     * 删除任务底下的case
     * @param suitId 测试套件
     */
    public void removeTestCaseOfSuit(Long suitId);
}
