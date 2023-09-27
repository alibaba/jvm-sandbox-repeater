package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.ReplayBizService;
import org.tony.console.biz.request.ReplayRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ReplayBO;
import org.tony.console.common.domain.ReplayParams;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.ReplayService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/replay")
public class ReplayApi {

    @Resource
    private ReplayService replayService;

    @Resource
    private ReplayBizService replayBizService;

    @RequestMapping(value = "detail", method = {RequestMethod.GET})
    @ResponseBody
    public Result<ReplayBO> detail(@RequestParam String repeatId) {
        return replayService.queryById(repeatId);
    }

    @RequestMapping("execute")
    @ResponseBody
    public Result<String> execute(
            @RequestBody ReplayRequest replayParams
    ) throws BizException {
        //默认都mock
        replayParams.setMock(true);
        replayParams.setSingle(true);
        return replayBizService.replay(replayParams);
    }

    @RequestMapping("executeCase")
    @ResponseBody
    public Result<String> executeCase(
            @RequestBody ReplayRequest request
    ) {
        try {
            request.setSingle(true);
            return replayBizService.replay(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }
}
