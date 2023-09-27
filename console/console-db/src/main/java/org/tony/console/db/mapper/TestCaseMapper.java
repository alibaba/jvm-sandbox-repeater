package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.TestCaseDO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/14 09:30
 */
@Mapper
public interface TestCaseMapper {

    public int insertCaseList(List<TestCaseDO> testCaseDOList);

    public List<TestCaseDO> selectTestCaseList(Map<String, Object> params);

    public TestCaseDO selectTestCaseById(String caseId);

    public Long count(Map<String, Object> params);

    public int removeCaseList(List<String> caseIdList);

    public int updateCaseList(List<TestCaseDO> testCaseDOList);

    public int removeTestCaseOfSuit(Long suitId);
}
