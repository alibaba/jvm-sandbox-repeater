package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;

/**
 * {@link ModuleConfigService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ModuleConfigService {

    PageResult<ModuleConfigBO> list(ModuleConfigParams params);

    RepeaterResult<ModuleConfigBO> query(ModuleConfigParams params);

    RepeaterResult<ModuleConfigBO> saveOrUpdate(ModuleConfigParams params);

    RepeaterResult<ModuleConfigBO> push(ModuleConfigParams params);
}
