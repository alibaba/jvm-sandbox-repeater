package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.AppGroovyConfigDO;

import java.util.List;

@Mapper
public interface AppGroovyMapper {

    public List<AppGroovyConfigDO> queryByApp(String app);

    public List<AppGroovyConfigDO> queryByAppWithoutContent(String app);

    public AppGroovyConfigDO queryById(Long id);

    public int updateContent(AppGroovyConfigDO appGroovyConfigDO);

    public int insert(AppGroovyConfigDO appGroovyConfigDO);
}
