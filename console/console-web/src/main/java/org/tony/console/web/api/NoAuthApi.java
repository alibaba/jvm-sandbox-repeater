package org.tony.console.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.TestCaseBizService;
import org.tony.console.common.Result;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/4 10:06
 */
@RestController
@RequestMapping("/noauth/api/v1/")
public class NoAuthApi {

    @Resource
    TestCaseBizService testCaseBizService;

    @ResponseBody
    @RequestMapping("/testCase/replayRecord")
    public Result<String> queryReplayRecord(@RequestParam("caseId") String caseId) {
        return testCaseBizService.queryTestCaseWrapperRecord(caseId);
    }
}
