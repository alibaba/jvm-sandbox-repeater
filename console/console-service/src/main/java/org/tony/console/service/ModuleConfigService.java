package org.tony.console.service;


import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.common.domain.ModuleConfigParams;
import org.tony.console.common.domain.PageResult;

public interface ModuleConfigService {

    PageResult<ModuleConfigBO> list(ModuleConfigParams params);

    Result<ModuleConfigBO> query(ModuleConfigParams params);

    Result<ModuleConfigBO> queryWithDefault(ModuleConfigParams params);

    Result<ModuleConfigBO> saveOrUpdate(ModuleConfigParams params);

    Result<ModuleConfigBO> push(ModuleConfigParams params);
}
