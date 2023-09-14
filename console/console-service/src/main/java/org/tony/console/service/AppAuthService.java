package org.tony.console.service;

import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.UserResourceDTO;

import java.util.List;

public interface AppAuthService {

    public List<UserResourceDTO> checkAuth(String appName, String user) throws BizException;
}
