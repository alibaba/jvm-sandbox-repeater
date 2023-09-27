package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.TestSuitDO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/14 11:15
 */
@Mapper
public interface TestSuitMapper {

    public int insert(TestSuitDO testSuit);

    public int update(TestSuitDO testSuitDO);

    public List<TestSuitDO> select(Map<String, Object> params);

    public TestSuitDO selectById(Long id);

    public int delete(Long id);
}
