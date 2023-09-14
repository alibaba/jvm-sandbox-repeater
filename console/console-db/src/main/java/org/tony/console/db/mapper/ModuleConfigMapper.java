package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.ModuleConfig;

import java.util.List;
import java.util.Map;

/**
*  @author author
*/
@Mapper
public interface ModuleConfigMapper{

    int insertModuleConfig(ModuleConfig object);

    int updateModuleConfig(ModuleConfig object);

    int update(ModuleConfig.UpdateBuilder object);

    List<ModuleConfig> queryModuleConfig(Map<String, Object> params);

    long count(Map<String, Object> params);

    ModuleConfig queryModuleConfigLimit1(Map<String, Object> params);
}