package com.alibaba.repeater.console.start.controller;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.service.RecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link RecordFacadeApi} Demo工程；作为repeater录制回放的数据存储
 * <p>
 *
 * @author zhaoyb1990
 */
@RestController
@RequestMapping("/facade/api")
public class RecordFacadeApi {

    @Resource
    private RecordService recordService;

    @RequestMapping(value = "record/{appName}/{traceId}", method = RequestMethod.GET)
    public RepeaterResult<String> getWrapperRecord(@PathVariable("appName") String appName,
                                                   @PathVariable("traceId") String traceId) {
        return recordService.get(appName, traceId);
    }

    @RequestMapping(value = "repeat/{appName}/{traceId}", method = RequestMethod.GET)
    public RepeaterResult<String> repeat(@PathVariable("appName") String appName,
                                         @PathVariable("traceId") String traceId,
                                         HttpServletRequest request) {
        return recordService.repeat(appName, traceId, request.getHeader("RepeatId"));
    }

    @RequestMapping(value = "record/save", method = RequestMethod.POST)
    public RepeaterResult<String> recordSave(@RequestBody String body) {
        return recordService.saveRecord(body);
    }

    @RequestMapping(value = "repeat/save", method = RequestMethod.POST)
    public RepeaterResult<String> repeatSave(@RequestBody String body) {
        return recordService.saveRepeat(body);
    }

    @RequestMapping(value = "repeat/callback/{repeatId}", method = RequestMethod.GET)
    public RepeaterResult<RepeatModel> callback(@PathVariable("repeatId") String repeatId) {
        return recordService.callback(repeatId);
    }

}
