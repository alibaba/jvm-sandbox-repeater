package org.tony.console.service;

import org.tony.console.service.model.app.AppDTO;
import org.tony.console.service.model.app.AppGroup;

import java.util.List;

/**
 * 应用服务
 */
public interface AppService {

    /**
     * 应用分组列表，根据bu来搞
     * @return
     */
    public List<AppGroup> queryAppGroupList(String name);

    /**
     * 根据id查询应用分组，带关联应用信息
     * @param buId 分组id
     * @return
     */
    public List<AppDTO> queryAppGroup(Integer buId);

    /**
     * 查询应用详情
     * @param name
     * @return
     */
    public AppDTO queryApp(String name);
}
