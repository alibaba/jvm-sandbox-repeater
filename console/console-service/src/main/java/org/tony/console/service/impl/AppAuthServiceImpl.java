package org.tony.console.service.impl;

import org.springframework.stereotype.Component;
import org.tony.console.common.enums.Status;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.AppAuthService;
import org.tony.console.service.UserResourceService;
import org.tony.console.service.config.BizConfig;
import org.tony.console.service.model.UserResourceDTO;
import org.tony.console.service.model.enums.ResourceType;
import org.tony.console.service.model.query.UserResourceQuery;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/29 14:20
 */
@Component
public class AppAuthServiceImpl implements AppAuthService {

    @Resource
    UserResourceService userResourceService;

    @Resource
    BizConfig bizConfig;

    @Override
    public List<UserResourceDTO> checkAuth(String appName, String user) throws BizException {
        UserResourceQuery query = new UserResourceQuery();
        query.setName(appName);
        query.setStatus(Status.VALID);
        query.setType(ResourceType.APP);

        //查询出所有的人
        List<UserResourceDTO> userResourceDTOList = userResourceService.search(query);
        if (userResourceDTOList.stream().anyMatch(item -> item.getUser().equalsIgnoreCase(user))) {
            return userResourceDTOList;
        }

        if (bizConfig.isSuperUser(user)) {
            return userResourceDTOList;
        }

        throw BizException.build("非管理员，没有该应用的操作权限");
    }
}
