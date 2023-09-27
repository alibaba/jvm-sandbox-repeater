package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.biz.model.TaskVO;
import org.tony.console.biz.model.TestCaseExecResultVO;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.biz.request.RunTestTaskFailBizRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.web.auth.UserInfoCache;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/5 16:01
 */
@RestController
@RequestMapping("/api/v1/testTask")
public class TaskApi {

    @Resource
    TestTaskBizService testTaskBizService;

    @ResponseBody
    @RequestMapping("create")
    public Result<Long> create(@RequestBody CreateTestTaskBizRequest request) {
        try {
            String user = UserInfoCache.getUser();
            request.setCreator(user);
            return testTaskBizService.createTask(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("runAgain")
    public Result runTaskFailAgain(@RequestBody RunTestTaskFailBizRequest request) {
        try {
             testTaskBizService.runTaskFail(request);
            return Result.buildSuccess(null, "成功");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("list")
    public PageResult<TaskVO> list(@RequestBody TaskQuery query) {
        return testTaskBizService.queryTask(query);
    }

    @ResponseBody
    @RequestMapping("listItem")
    public PageResult<TestCaseExecResultVO> list(@RequestBody TaskItemQuery query) {
        return testTaskBizService.queryTaskItem(query);
    }

    @ResponseBody
    @RequestMapping("detail")
    public Result<TaskVO> queryById(@RequestParam Long id) {
        return Result.buildSuccess(testTaskBizService.queryById(id), "success");
    }
}
