package org.tony.console.service;

import org.tony.console.common.Result;
import org.tony.console.common.domain.ReplayBO;
import org.tony.console.common.domain.ReplayParams;

public interface ReplayService {

    Result<String> replay(ReplayParams params);

    Result<String> saveRepeat(String body);

    Result<ReplayBO> query(ReplayParams params);

    Result<ReplayBO> queryById(String repeatId);
}
