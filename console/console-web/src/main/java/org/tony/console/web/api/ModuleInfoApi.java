package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.enums.Env;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.web.model.FlushModuleRequest;
import org.tony.console.web.model.RemoveModuleRequest;
import org.tony.console.web.model.ReportRequest;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/module")
public class ModuleInfoApi {

    @Resource
    ModuleInfoService moduleInfoService;

    @ResponseBody
    @RequestMapping("/byApp")
    public Result<List<ModuleInfoBO>> queryModule(
            @RequestParam("appName") String appName,
            @RequestParam(value = "env", required = false) String env
    ) {

        ModuleInfoQuery moduleInfoQuery = new ModuleInfoQuery();
        moduleInfoQuery.setAppName(appName);
        moduleInfoQuery.setEnvironment(env);
        List<ModuleInfoBO> list = moduleInfoService.queryV2(moduleInfoQuery);

        List<ModuleInfoBO> f = list.stream().filter(item->item.getStatus().equals(ModuleStatus.ACTIVE)).collect(Collectors.toList());
        return Result.buildSuccess(f, "查询成功");
    }

    @ResponseBody
    @RequestMapping("/report.json")
    public Result<ModuleInfoBO> report(@ModelAttribute("requestParams") ReportRequest request) {
        ModuleInfoBO moduleInfoBO = new ModuleInfoBO();
        moduleInfoBO.setStatus(request.getStatus());
        moduleInfoBO.setAppName(request.getAppName());
        moduleInfoBO.setIp(request.getIp());
        moduleInfoBO.setPort(request.getPort());
        moduleInfoBO.setVersion(request.getVersion());
        moduleInfoBO.setEnvironment(Env.fromString(request.getEnvironment().toUpperCase()));
        return moduleInfoService.report(moduleInfoBO);
    }

    @RequestMapping("/list")
    public PageResult<ModuleInfoBO> list(@RequestBody ModuleInfoQuery query) {
        return moduleInfoService.query(query);
    }

    @RequestMapping("/flush")
    public Result flush(@RequestBody FlushModuleRequest request) {
        try {
            request.check();
        } catch (BizException e) {
            return Result.buildFail("param error");
        }
        return moduleInfoService.flush(request.getModuleInfoBOList());
    }

    @RequestMapping("/remove")
    public Result remove(@RequestBody RemoveModuleRequest request) {
        try {
            request.check();
        } catch (BizException e) {
            return Result.buildFail("param error");
        }
        return moduleInfoService.remove(request.getModuleInfoBOList());
    }
}
