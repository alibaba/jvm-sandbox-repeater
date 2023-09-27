package org.tony.console.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.FeiShuBizService;
import org.tony.console.biz.RecordBizService;
import org.tony.console.biz.TagBizService;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.biz.job.DataCleanJob;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.mapper.RecordMapper;
import org.tony.console.service.TaskService;
import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.web.service.DataCleanService;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author peng.hu1
 * @Date 2023/1/6 14:59
 */
@Slf4j
@RestController
@RequestMapping("/test/mock")
public class TestMockApi {

    @Resource
    TaskService taskService;

    @Resource
    TestTaskBizService testTaskBizService;

    @Resource
    FeiShuBizService feiShuBizService;

    @Resource
    TagBizService tagBizService;

    @Resource
    DataCleanJob dataCleanJob;

    @Resource
    RecordBizService recordBizService;

    @Resource
    DataCleanService dataCleanService;

    @Resource
    RecordMapper recordMapper;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @ResponseBody
    @RequestMapping("runTaskItem")
    public Result runTaskItem(@RequestParam Long id) throws BizException {
        TaskItemDTO taskItemDTO = taskService.queryItemById(id);

        testTaskBizService.runTaskItem(taskItemDTO.getTaskId(), taskItemDTO);

        return Result.buildSuccess(id, "成功");
    }

    @ResponseBody
    @RequestMapping("sendFeiShu")
    public Result sendFeiShu(@RequestParam Long taskId) throws BizException {

        feiShuBizService.sendTaskReport(taskId);

        return Result.buildSuccess(taskId, "成功");
    }

    @ResponseBody
    @RequestMapping("testTagCompute")
    public Result testTagCompute(@RequestParam String appName, @RequestParam String traceId) throws BizException {

        return Result.buildSuccess( tagBizService.compute(appName, traceId), "成功");
    }

    @ResponseBody
    @RequestMapping("cleanData")
    public Result clean() {
        dataCleanJob.cleanRecord();
        return Result.buildSuccess(null, "成功");
    }

    @ResponseBody
    @RequestMapping("testRepeat")
    public Result testRepeat(@RequestParam String body) throws BizException {
        recordBizService.saveRepeat(body);

        return Result.buildSuccess("成功");
    }

    @ResponseBody
    @RequestMapping("cleanAppData")
    public Result testRepeat(@RequestParam String appName, @RequestParam Integer size) throws BizException, InterruptedException, ExecutionException {
        dataCleanService.cleanData(appName, size);

        return Result.buildSuccess("成功");
    }

    @ResponseBody
    @RequestMapping("removeRecord")
    public Result removeRecord(@RequestParam String appName) throws BizException {

        executorService.execute(()->{
            for (int i=1; i<=600; i++) {
                recordMapper.removeBatch(appName);
                log.info("remove record num={}", i*10000);
            }
        });

        return Result.buildSuccess("成功");
    }
}
