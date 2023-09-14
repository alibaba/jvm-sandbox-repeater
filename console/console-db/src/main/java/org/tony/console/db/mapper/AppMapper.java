package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.App;
import org.tony.console.db.model.Bu;

import java.util.List;

@Mapper
public interface AppMapper {

    public int insert(App app);

    public List<App> searchAppList(String name);

    public List<App> queryByIdList(List<Long> idList);

    public List<App> queryByBuId(Integer buId);

    public App selectByName(String name);

    public List<Bu> queryBuList();

    public int update(App app);
}
