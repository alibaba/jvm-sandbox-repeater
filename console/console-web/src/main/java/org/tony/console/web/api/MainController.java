package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.common.Result;
import org.tony.console.web.auth.AuthUtil;
import org.tony.console.web.auth.UserInfo;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/2/4 16:42
 */
@RestController
@RequestMapping("main")
public class MainController {

    @Resource
    AuthUtil authUtil;

    @ResponseBody
    @RequestMapping(value = "base", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryBaseInfo(String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("sso_authorize", authUtil.getAuthorizeUrl());
        map.put("client_id", authUtil.getClientId());

        return Result.buildSuccess(map, "success");
    }

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public Result login(@RequestParam String code, @RequestParam String redirectUri) {
        UserInfo userInfo = authUtil.loginWithCode(code, redirectUri);

        return Result.buildSuccess(userInfo, "success");
    }
}
