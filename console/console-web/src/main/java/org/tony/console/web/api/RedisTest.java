package org.tony.console.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.redis.RedisUtil;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/2/15 17:15
 */
@RestController
@RequestMapping("/test/v1/redis")
public class RedisTest {

    @Resource
    RedisUtil redisUtil;

    @ResponseBody
    @RequestMapping("run")
    public Result runTaskItem(@RequestParam int i) throws BizException {
        redisUtil.setIfAbsent("A", "1");
        redisUtil.get("A");
        redisUtil.del("A");
        return Result.buildSuccess("success", "success");
    }
}
