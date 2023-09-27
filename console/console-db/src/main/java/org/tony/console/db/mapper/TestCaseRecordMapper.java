package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.TestCaseRecordDO;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestCaseRecordMapper {

    public void insert(List<TestCaseRecordDO> testCaseRecordDOList);

    public TestCaseRecordDO selectById(long recordId);

    public List<TestCaseRecordDO> select(Map<String, Object> params);

    public int remove(List<String> caseIdList);

    public TestCaseRecordDO selectByCaseId(String caseId);

    public int update(TestCaseRecordDO testCaseRecordDO);
}
