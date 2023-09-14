package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.AppBizService;
import org.tony.console.biz.model.TestSetConfig;
import org.tony.console.biz.request.app.AddAppRequest;
import org.tony.console.biz.request.app.UpdateAdminRequest;
import org.tony.console.biz.request.app.UpdateDailyTestRequest;
import org.tony.console.biz.request.app.UpdateTestSetRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.mapper.AppMapper;
import org.tony.console.db.model.App;
import org.tony.console.db.model.Bu;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.AppService;
import org.tony.console.service.model.app.AppDTO;
import org.tony.console.service.model.app.AppGroup;
import org.tony.console.service.model.config.AppDailyTestConfigDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;
import org.tony.console.web.auth.UserInfoCache;
import org.tony.console.web.model.AppInfo;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/app")
public class AppAdminApi {

    @Resource
    AppMapper appMapper;

    @Resource
    AppBizService appBizService;

    @Resource
    AppConfigService appConfigService;

    @Resource
    AppService appService;

    @RequestMapping(value = "queryGroup", method = RequestMethod.GET)
    public Result<List<AppGroup>> queryGroup(@RequestParam(required = false) String name) {
        return Result.builder().data(appService.queryAppGroupList(name)).success(true).build();
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    public Result<List<App>> queryAppList(@RequestParam(required = false) String name) {
        return Result.builder().data(appMapper.searchAppList(name)).success(true).build();
    }

    @RequestMapping(value = "list/admin", method = RequestMethod.GET)
    public Result<List<App>> queryAppListByAdmin() {
        return Result.builder().data(appBizService.queryMyApp(UserInfoCache.getUser())).success(true).build();
    }

    @RequestMapping(value = "list/bu", method = RequestMethod.GET)
    public Result<List<Bu>> queryBuList() {
        return Result.builder().data(appBizService.queryBu()).success(true).build();
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public Result<AppDTO> queryAppAdminList(@RequestParam String appName) {
        return Result.buildSuccess(appBizService.queryApp(appName), "success");
    }

    @RequestMapping(value = "admin/update", method = RequestMethod.POST)
    public Result queryAppAdminList(@RequestBody UpdateAdminRequest request) {
        request.setOperator(UserInfoCache.getUser());
        try {
            appBizService.updateAdmin(request);
            appBizService.updateBu(request.getAppName(), request.getBuId());
            return Result.buildSuccess(null, "success");
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Result addAppApi(@RequestBody AddAppRequest request) throws BizException {
        appBizService.addApp(request);
        return Result.buildSuccess("添加成功");
    }

    @RequestMapping(value = "testSet/list", method = RequestMethod.GET)
    public Result<TestSetConfig> queryAppTestTaskSet(
            @RequestParam String appName,
            @RequestParam String env
    ) {
        return Result.buildSuccess(
                appBizService.queryTestTaskSet(appName, Env.fromString(env)),
                "success"
        );
    }

    @RequestMapping(value = "testSet/update", method = RequestMethod.POST)
    public Result<AppTestTaskSetDTO> updateAppTestTaskSet(
            @RequestBody UpdateTestSetRequest request
    ) {
        request.setOperator(UserInfoCache.getUser());

        try {
            appBizService.updateTestTaskSet(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
        return Result.buildSuccess(null, "success");
    }

    @RequestMapping(value = "dailyTest/list", method = RequestMethod.GET)
    public Result<AppDailyTestConfigDTO> queryAppDailyTestConfig(
            @RequestParam String appName
    ) {
        return Result.buildSuccess(
                appConfigService.queryDailyTest(appName),
                "success"
        );
    }

    @RequestMapping(value = "dailyTest/update", method = RequestMethod.POST)
    public Result<AppTestTaskSetDTO> updateAppDailyTest(
            @RequestBody UpdateDailyTestRequest request
    ) {
        request.setOperator(UserInfoCache.getUser());

        try {
            appBizService.updateDailyTest(request);
        } catch (BizException e) {
            return Result.buildFail(e.getMessage());
        }
        return Result.buildSuccess(null, "success");
    }

    @RequestMapping(value = "search/luban", method = RequestMethod.GET)
    public Result<List<AppInfo>> searchServiceList(@RequestParam String name) {

        List<AppInfo> appInfoList = new LinkedList<>();

        AppInfo appInfo = new AppInfo();
        appInfo.setId(1L);
        appInfo.setName("test");
        appInfo.setMgr_list("xiuzhu");

        appInfoList.add(appInfo);

        return Result.buildSuccess(appInfoList, "查询成功");
    }
}
