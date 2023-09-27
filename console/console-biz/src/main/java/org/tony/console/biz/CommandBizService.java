package org.tony.console.biz;

import org.tony.console.biz.request.Command;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2023/5/15 14:33
 */
public interface CommandBizService {

    /**
     * 执行命令
     * @param command
     */
    public void execute(Command command) throws BizException;
}
