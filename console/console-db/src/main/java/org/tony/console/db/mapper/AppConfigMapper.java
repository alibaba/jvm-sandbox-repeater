package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tony.console.db.model.AppConfig;

import java.util.List;

@Mapper
public interface AppConfigMapper {

    public List<AppConfig> queryAppConfig(@Param("appName") String appName, @Param("type") int type, @Param("env") String env);

    public int insert(AppConfig appConfig);

    public int update(AppConfig appConfig);

    public List<AppConfig> queryAppConfigByTye(int type);
}
