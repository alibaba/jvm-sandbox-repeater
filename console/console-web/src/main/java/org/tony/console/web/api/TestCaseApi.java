package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.TestCaseBizService;
import org.tony.console.biz.TestSuitBizService;
import org.tony.console.biz.model.TestCaseCompareSortVO;
import org.tony.console.biz.request.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.TestCaseDetailDTO;
import org.tony.console.web.auth.UserInfoCache;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2022/12/16 10:28
 */
@RestController
@RequestMapping("/api/v1/testCase")
public class TestCaseApi {

    @Resource
    TestSuitBizService testSuitBizService;

    @Resource
    TestCaseBizService testCaseBizService;

    @ResponseBody
    @RequestMapping("query")
    public PageResult<TestCaseDTO> query(@RequestBody QueryTestCaseRequest request) {
        try {
            request.check();
            return testSuitBizService.queryTestCase(request);
        } catch (BizException e) {
            return PageResult.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("remove")
    public Result remove(@RequestBody RemoveTestCaseBizRequest request) {
        try {
            request.check();
            return testCaseBizService.removeTestCase(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("detail")
    public Result<TestCaseDetailDTO> queryDetail(@RequestParam("id") String id) {
        return testSuitBizService.queryTestCaseDetail(id);
    }

    @ResponseBody
    @RequestMapping("replay")
    public Result<String> queryReplayRecord(@RequestParam("caseId") String caseId) {
        return testCaseBizService.queryTestCaseWrapperRecord(caseId);
    }

    @ResponseBody
    @RequestMapping("queryCSortConfig")
    public Result<TestCaseCompareSortVO> queryCSortConfig(@RequestParam("caseId") String caseId) {
        return Result.buildSuccess(testCaseBizService.queryCompareSortConfig(caseId), "success");
    }

    @ResponseBody
    @RequestMapping("updateCSortConfig")
    public Result<TestCaseCompareSortVO> updateCSortConfig(@RequestBody TestCaseCompareSortVO vo) {
        testCaseBizService.updateCompareSortConfig(vo);
        return Result.buildSuccess( "success");
    }

    @ResponseBody
    @RequestMapping("add")
    public Result addTestCase(@RequestBody AddTestCaseRequest request) {
        try {
            String user = UserInfoCache.getUser();
            request.setUser(user);
            return testSuitBizService.addTestCase(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("suit/add")
    public Result addTestSuit(@RequestBody AddTestSuitBizRequest request) {
        try {
            if(request.getParentId()==null) {
                request.setParentId(0L);
            }
            String user = UserInfoCache.getUser();
            request.setUser(user);
            return testSuitBizService.addTestSuit(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("suit/remove")
    public Result removeTestSuit(@RequestBody RemoveTestSuitRequest request) {
        try {
            String user = UserInfoCache.getUser();
            request.setUser(user);
            return testSuitBizService.removeTestSuit(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("suit/tree")
    public Result listSuitTree(@RequestParam String appName) {
        return testSuitBizService.queryTestSuitTree(appName);
    }


    @ResponseBody
    @RequestMapping(value = "task/search", method = RequestMethod.GET)
    public Result searchTask(@RequestParam String appName, @RequestParam(required = false) String value) {
        return testSuitBizService.searchSuitTask(appName, value);
    }

    @ResponseBody
    @RequestMapping("replace/response")
    public Result replace(@RequestBody ReplaceRespRequest request) {
        try {
            return testCaseBizService.replaceResponse(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("replace/subInvoke")
    public Result replaceSubInvocation(@RequestBody ReplaceSubInvocationRequest request) {
        try {
            return testCaseBizService.replaceSubInvocation(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("case/move")
    public Result moveCase(@RequestBody MoveTestCaseBizRequest request) {
        try {
            String user = UserInfoCache.getUser();
            request.setOperator(user);
            return testSuitBizService.moveTestCase(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

}
