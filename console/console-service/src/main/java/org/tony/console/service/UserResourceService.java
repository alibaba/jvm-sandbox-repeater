package org.tony.console.service;

import org.tony.console.service.model.query.UserResourceQuery;
import org.tony.console.service.model.UserResourceDTO;

import java.util.List;

public interface UserResourceService {

    /**
     * 添加用户资源
     * @param userResourceDTOList 列表
     * @return
     */
    public int add(List<UserResourceDTO> userResourceDTOList);

    /**
     * 删除用户资源
     * @param userResourceDTOList
     * @return
     */
    public int remove(List<UserResourceDTO> userResourceDTOList);

    /**
     * 查询用户资源信息
     * @param query 查询
     * @return
     */
    public List<UserResourceDTO> search(UserResourceQuery query);
}
