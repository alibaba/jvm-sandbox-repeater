package org.tony.console.biz;

import org.tony.console.biz.model.TestCaseCompareSortVO;
import org.tony.console.biz.request.AddTestCaseBizRequest;
import org.tony.console.biz.request.RemoveTestCaseBizRequest;
import org.tony.console.biz.request.ReplaceRespRequest;
import org.tony.console.biz.request.ReplaceSubInvocationRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/15 17:38
 */
public interface TestCaseBizService {

    public Result add(AddTestCaseBizRequest request) throws BizException;

    /**
     * 查询用例详情
     * @param caseId 用例id
     * @return
     */
    public Result<String> queryTestCaseWrapperRecord(String caseId);

    /**
     * 删除测试case
     * @return
     */
    public Result removeTestCase(RemoveTestCaseBizRequest request);


    /**
     * 替换自动化case的返回结果
     * @param request
     * @return
     */
    public Result replaceResponse(ReplaceRespRequest request) throws BizException;

    /**
     * 替换自动化case的返回结果
     * @param request
     * @return
     */
    public Result replaceSubInvocation(ReplaceSubInvocationRequest request) throws BizException;


    /**
     * 查询排序配置
     * @param caseId 用例id
     * @return
     */
    public TestCaseCompareSortVO queryCompareSortConfig(String caseId);

    /**
     * 更新排序配置
     * @param vo 配置对象
     * @return
     */
    public void updateCompareSortConfig(TestCaseCompareSortVO vo);
}
