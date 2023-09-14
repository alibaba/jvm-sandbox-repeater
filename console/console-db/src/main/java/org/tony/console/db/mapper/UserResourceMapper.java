package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.UserResourceDO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:03
 */
@Mapper
public interface UserResourceMapper {


    /**
     * 插入
     * @param userResourceDO 资源
     * @return
     */
    public int insert(UserResourceDO userResourceDO);

    /**
     * 用户资源，查询
     * @param params
     * @return
     */
    public List<UserResourceDO> select(Map<String, Object> params);


    public int delete(List<Long> idList);
}
