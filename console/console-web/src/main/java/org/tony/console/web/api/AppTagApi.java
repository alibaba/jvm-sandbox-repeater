package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.biz.TagConfigBizService;
import org.tony.console.biz.request.AddTagBizRequest;
import org.tony.console.biz.request.tag.DeleteTagRequest;
import org.tony.console.biz.request.tag.UpdateTagRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.App;
import org.tony.console.web.auth.UserInfoCache;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/17 12:57
 */
@RestController
@RequestMapping("/api/v1/tag")
public class AppTagApi {

    @Resource
    TagConfigBizService tagConfigBizService;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Result<List<App>> addTag(@RequestBody AddTagBizRequest request) throws BizException {
        request.setUser(UserInfoCache.getUser());
        tagConfigBizService.addTag(request);
        return Result.buildSuccess("添加成功");
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    public Result<List> queryTagList(String appName) {
        return Result.buildSuccess(tagConfigBizService.queryTagList(appName), "success");
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result updateTag(@RequestBody UpdateTagRequest request) throws BizException {
        request.setOperator(UserInfoCache.getUser());
        tagConfigBizService.updateTag(request);
        return Result.buildSuccess("更新成功");
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public Result remove(@RequestBody DeleteTagRequest request) throws BizException {
        request.setOperator(UserInfoCache.getUser());
        tagConfigBizService.removeTag(request);
        return Result.buildSuccess("删除成功");
    }
}
