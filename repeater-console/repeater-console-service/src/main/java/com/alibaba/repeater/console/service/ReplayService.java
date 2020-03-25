package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.params.ReplayParams;

/**
 * {@link ReplayService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ReplayService {

    RepeaterResult<String> replay(ReplayParams params);

    RepeaterResult<String> saveRepeat(String body);

    RepeaterResult<ReplayBO> query(ReplayParams params);
}
