package org.tony.console.biz.components;

import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/16 16:12
 */
public abstract class BizTemplate {

    public static ThreadLocal<BizSession> sessionThreadLocal = new ThreadLocal<>();

    public void execute() throws BizException {
        try {
            BizSession session = new BizSession();
            sessionThreadLocal.set(session);
            execute(session);
        } finally {
            sessionThreadLocal.set(null);
        }
    }

    public abstract void execute(BizSession session) throws BizException;


    public static BizSession getSession() {
        return sessionThreadLocal.get();
    }
}
