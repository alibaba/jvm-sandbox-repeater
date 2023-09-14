package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.TagConfigDO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:22
 */
@Mapper
public interface TagConfigMapper {

    /**
     * 查询
     * @param params
     * @return
     */
    public List<TagConfigDO> select(Map<String, Object> params);

    public TagConfigDO selectById(Long id);

    public int insert(TagConfigDO tagConfigDO);

    public int update(TagConfigDO tagConfigDO);

    public int delete(Long id);
}
