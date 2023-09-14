package org.tony.console.biz.components;

import org.tony.console.common.exception.BizException;

public interface BizComService<Request> {

    public void execute(Request request) throws BizException;

    public boolean isSupport(Request request);

}
