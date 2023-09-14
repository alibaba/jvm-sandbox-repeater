package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tony.console.db.model.ModuleInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface ModuleInfoMapper {
    int insertModuleInfo(ModuleInfo object);

    int updateModuleInfo(ModuleInfo object);

    List<ModuleInfo> queryModuleInfo(Map<String, Object> params);

    Long count(Map<String, Object> params);

    ModuleInfo queryModuleInfoLimit1(ModuleInfo object);

    int deleteById(Long id);
}
