package org.tony.console.biz;

import org.tony.console.biz.model.TestSuitTreeVO;
import org.tony.console.biz.request.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.TestCaseDetailDTO;
import org.tony.console.service.model.TestSuitDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 19:04
 */
public interface TestSuitBizService {

    /**
     * 添加测试套件
     * @param addTestSuitBizRequest 请求
     * @return
     */
    public Result addTestSuit(AddTestSuitBizRequest addTestSuitBizRequest) throws BizException;

    /**
     * 清除一个测试套件
     * @param request 请求
     * @return
     * @throws BizException
     */
    public Result removeTestSuit(RemoveTestSuitRequest request) throws BizException;

    /**
     * 测试套件组装成一个树
     * @return
     */
    public Result<List<TestSuitTreeVO>> queryTestSuitTree(String appName);

    /**
     * 搜索应用的type = task的testSuit
     * @param appName
     * @param key
     * @return
     */
    public Result<List<TestSuitDTO>> searchSuitTask(String appName, String key);


    /**
     * 添加测试用例
     * @param request 请求
     * @return
     */
    public Result addTestCase(AddTestCaseRequest request) throws BizException;

    /**
     * 查询测试用例
     * @param request
     * @return
     */
    public PageResult<TestCaseDTO> queryTestCase(QueryTestCaseRequest request);

    /**
     * 查询测试用例详情
     * @param caseId
     * @return
     */
    public Result<TestCaseDetailDTO> queryTestCaseDetail(String caseId);

    /**
     * 添加全网回归
     * @param taskId
     * @return
     */
    public Result addRegression(Long taskId) throws BizException;

    /**
     * 剔除全网回归
     * @param taskId
     * @return
     */
    public Result rmvRegression(Long taskId) throws BizException;

    /**
     * 移动测试用例
     * @param request 请求
     * @return
     * @throws BizException
     */
    public Result moveTestCase(MoveTestCaseBizRequest request) throws BizException;
}
