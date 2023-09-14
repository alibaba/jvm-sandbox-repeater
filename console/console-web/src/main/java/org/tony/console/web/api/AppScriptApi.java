package org.tony.console.web.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.AppConfigBizService;
import org.tony.console.biz.request.app.SaveGroovyContentRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.groovy.GroovyConfigDTO;
import org.tony.console.web.auth.UserInfoCache;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/29 10:30
 */
@RequestMapping("/api/v1/script")
@RestController
public class AppScriptApi {

    @Resource
    AppConfigBizService appConfigBizService;

    @RequestMapping("groovy/list")
    public Result<List<GroovyConfigDTO>> queryGroovyList(String appName) {

        return Result.buildSuccess(appConfigBizService.queryGroovyList(appName), "success");
    }

    @RequestMapping("groovy/detail")
    public Result<GroovyConfigDTO> groovyDetail(Long id) {
        return Result.buildSuccess(appConfigBizService.queryGroovyById(id), "success");
    }


    @RequestMapping("groovy/update")
    public Result<GroovyConfigDTO> groovyDetail(@RequestBody SaveGroovyContentRequest request) throws BizException {
        request.setUser(UserInfoCache.getUser());
        appConfigBizService.saveGroovyContent(request);
        return Result.buildSuccess("success");
    }
}
