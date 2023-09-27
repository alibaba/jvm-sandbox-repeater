package org.tony.console.web.facade;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.CommandBizService;
import org.tony.console.biz.request.Command;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/5/15 11:08
 */
@RestController
@RequestMapping("/facade/command")
public class ReplayFacade {

    @Resource
    CommandBizService commandBizService;

    @RequestMapping("execute")
    public Result<String> execute(@RequestBody Command command) throws BizException {
        command.check();
        commandBizService.execute(command);

        return Result.buildSuccess("success");
    }
}
