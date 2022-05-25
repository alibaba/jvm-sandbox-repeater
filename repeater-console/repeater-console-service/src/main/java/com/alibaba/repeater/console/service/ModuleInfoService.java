package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleInfoParams;

import java.util.List;

/**
 * {@link ModuleInfoService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ModuleInfoService {

    PageResult<ModuleInfoBO> query(ModuleInfoParams params);

    RepeaterResult<List<ModuleInfoBO>> query(String appName);

    RepeaterResult<ModuleInfoBO> query(String appName, String ip);

    RepeaterResult<ModuleInfoBO> report(ModuleInfoBO params);

    RepeaterResult<ModuleInfoBO> active(ModuleInfoParams params);

    RepeaterResult<ModuleInfoBO> frozen(ModuleInfoParams params);

    RepeaterResult<String> install(ModuleInfoParams params);

    RepeaterResult<String> reload(ModuleInfoParams params);
}
