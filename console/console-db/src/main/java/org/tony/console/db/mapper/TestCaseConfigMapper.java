package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.tony.console.db.model.TestCaseConfig;

/**
 * @author peng.hu1
 * @Date 2023/9/5 10:29
 */
@Mapper
public interface TestCaseConfigMapper {

    public int insert(TestCaseConfig testCaseConfig);

    public int delete(Long id);

    public int update(TestCaseConfig config);

    @Select("SELECT * FROM test_case_config where case_id=#{caseId} and `type`=#{type} LIMIT 1")
    public TestCaseConfig selectOne(@Param("caseId") String caseId, @Param("type")int type);
}
