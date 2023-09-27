package org.tony.console.db.dao;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.tony.console.common.Page;
import org.tony.console.db.mapper.ModuleConfigMapper;
import org.tony.console.db.model.ModuleConfig;
import org.tony.console.db.query.ModuleConfigQuery;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModuleConfigDao {

    @Resource
    ModuleConfigMapper moduleConfigMapper;

    public Page<ModuleConfig> selectByParams(final ModuleConfigQuery query) {

        Long total = moduleConfigMapper.count(query.toParams());
        List<ModuleConfig> moduleConfigs = moduleConfigMapper.queryModuleConfig(query.toParams());

        return Page.build(moduleConfigs, total);
    }

    public ModuleConfig query(final ModuleConfigQuery query) {
        return moduleConfigMapper.queryModuleConfigLimit1(query.toParams());
    }

    public ModuleConfig saveOrUpdate(ModuleConfig params) {

        if (params.getId()!=null) {
            moduleConfigMapper.updateModuleConfig(params);
        } else {
            moduleConfigMapper.insertModuleConfig(params);
        }

        return params;
    }

    public ModuleConfig queryById(Long id) {
        Map<String, Object> params = ImmutableMap.<String, Object> builder()
                .put("id", id)
                .build();
        return moduleConfigMapper.queryModuleConfigLimit1(params);
    }
}
