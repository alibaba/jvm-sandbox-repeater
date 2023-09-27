package org.tony.console.web.facade;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.RecordBizService;
import org.tony.console.common.Result;
import org.tony.console.common.domain.RepeatModel;
import org.tony.console.common.domain.ReplayParams;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.RecordService;
import org.tony.console.service.ReplayService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/facade/api")
public class PersistenceFacadeApi {

    @Resource
    private RecordService recordService;

    @Resource
    private ReplayService replayService;

    @Resource
    RecordBizService recordBizService;

    @RequestMapping(value = "record/{appName}/{traceId}", method = RequestMethod.GET)
    public Result<String> getWrapperRecord(@PathVariable("appName") String appName,
                                           @PathVariable("traceId") String traceId) {
        return recordService.get(appName, traceId);
    }

    @RequestMapping(value = "repeat/{appName}/{ip}/{traceId}", method = RequestMethod.GET)
    public Result<String> repeat(@PathVariable("appName") String appName,
                                         @PathVariable("ip") String ip,
                                         @PathVariable("traceId") String traceId,
                                         HttpServletRequest request) {
        // fix issue #63
        ReplayParams params = ReplayParams.builder()
                .repeatId(request.getHeader("RepeatId"))
                .ip(ip)
                .build();
        params.setAppName(appName);
        params.setTraceId(traceId);
        return replayService.replay(params);
    }

    @RequestMapping(value = "record/save", method = RequestMethod.POST)
    public Result<String> recordSave(@RequestBody String body) {
        try {
            recordBizService.saveRecord(body);
            return Result.buildSuccess("-/-", "sucess");
        } catch (BizException e) {
            return Result.buildFail("保存失败");
        }
    }

    @RequestMapping(value = "repeat/save", method = RequestMethod.POST)
    public Result<String> repeatSave(@RequestBody String body) {
        return replayService.saveRepeat(body);
    }

    @RequestMapping(value = "repeat/callback/{repeatId}", method = RequestMethod.GET)
    public Result<RepeatModel> callback(@PathVariable("repeatId") String repeatId) {
        return recordService.callback(repeatId);
    }
}
