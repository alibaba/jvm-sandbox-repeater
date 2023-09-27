package org.tony.console.service;

import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.TestSuitQuery;
import org.tony.console.service.model.TestSuitDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:10
 */
public interface TestSuitService {


    public List<TestSuitDTO> queryAll(String appName);


    public TestSuitDTO addTestSuit(TestSuitDTO testSuitDTO);

    /**
     * 测试套件
     * @param id
     * @return
     */
    public TestSuitDTO queryById(Long id);

    /**
     * 搜索testSuit
     * @param query 查询请求
     * @return
     */
    public List<TestSuitDTO> search(TestSuitQuery query);

    /**
     * 删除测试套件
     * @param suitId
     */
    public void removeSuit(Long suitId) throws BizException;
}
