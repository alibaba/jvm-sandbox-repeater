package org.tony.console.biz.Impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.biz.CommandBizService;
import org.tony.console.biz.request.Command;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2023/5/15 14:35
 */

@Slf4j
@Component
public class CommandBizServiceImpl implements CommandBizService {

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    @Override
    public void execute(Command command) throws BizException {
        log.info("execute command = {}", command);
        try {
            switch (command.getType()) {
                case 0:
                    replay(command);
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            log.error("command error = {}", command, e);
            throw e;
        }

    }

    /**
     * 回放命令
     * @param command
     */
    private void replay(Command command) throws BizException {
        HttpUtil.Resp resp = HttpUtil.doPost(String.format(repeatURL,command.getIp(),command.getPort()), command.getRequestParams());
        if (!resp.isSuccess()) {
            throw BizException.build(resp.getMessage());
        }
    }
}
