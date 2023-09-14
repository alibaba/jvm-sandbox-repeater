package org.tony.console.service;

import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleInfoParams;
import org.tony.console.common.domain.PageResult;
import org.tony.console.db.query.ModuleInfoQuery;


import java.util.List;

/**
 * {@link ModuleInfoService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ModuleInfoService {

    PageResult<ModuleInfoBO> query(ModuleInfoQuery query);

    List<ModuleInfoBO> queryV2(ModuleInfoQuery query);

    Result<List<ModuleInfoBO>> query(String appName);

    Result<ModuleInfoBO> query(String appName, String ip);

    Result<ModuleInfoBO> report(ModuleInfoBO params);

    Result<ModuleInfoBO> active(ModuleInfoParams params);

    Result<ModuleInfoBO> frozen(ModuleInfoParams params);

    Result<String> install(ModuleInfoParams params);

    Result<String> reload(ModuleInfoParams params);

    Result flush(List<ModuleInfoBO> moduleInfoBOList);

    Result remove(List<ModuleInfoBO> moduleInfoBOList);
}
