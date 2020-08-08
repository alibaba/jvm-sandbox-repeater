package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;

import java.util.List;

/**
 * {@link ModuleConfigService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ModuleConfigService {

    List<ModuleConfigBO> list(Long appId);

    RepeaterResult<ModuleConfigBO> query(ModuleConfigParams params);

    RepeaterResult<ModuleConfigBO> saveOrUpdate(ModuleConfigParams params);

    RepeaterResult<ModuleConfigBO> push(ModuleConfigParams params);
}
