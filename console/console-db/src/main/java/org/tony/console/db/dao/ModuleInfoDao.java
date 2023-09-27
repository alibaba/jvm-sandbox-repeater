package org.tony.console.db.dao;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.Page;
import org.tony.console.db.mapper.ModuleInfoMapper;
import org.tony.console.db.model.ModuleInfo;
import org.tony.console.db.query.ModuleInfoQuery;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
public class ModuleInfoDao {

    @Resource
    ModuleInfoMapper moduleInfoMapper;

    public List<ModuleInfo> findByAppName(String appName) {
        ModuleInfoQuery query = new ModuleInfoQuery(appName, null, null);
        return moduleInfoMapper.queryModuleInfo(
                query.toParams()
        );
    }

    public List<ModuleInfo> query(ModuleInfoQuery query) {
        return moduleInfoMapper.queryModuleInfo(query.toParams());
    }

    public Page<ModuleInfo> selectByParams(ModuleInfoQuery query) {

        Map<String, Object> params = query.toParams();
        Long total = moduleInfoMapper.count(params);

        if (total>0) {
            List<ModuleInfo> moduleInfos = moduleInfoMapper.queryModuleInfo(params);
            return Page.build(moduleInfos, total);
        }

        return Page.build(Collections.<ModuleInfo>emptyList(), total);
    }

    public ModuleInfo save(ModuleInfo params) {
        Assert.notNull(params.getAppName(), "appName不可以为空");
        Assert.notNull(params.getIp(), "ip不可以为空");

        ModuleInfoQuery query = new ModuleInfoQuery(params.getAppName(), params.getIp(), null);
        List<ModuleInfo> moduleInfos = moduleInfoMapper.queryModuleInfo(
                query.toParams()
        );

        if (CollectionUtils.isEmpty(moduleInfos)) {
            moduleInfoMapper.insertModuleInfo(params);
            return params;
        }

        ModuleInfo moduleInfo = moduleInfos.get(0);
        moduleInfo.setStatus(params.getStatus());
        moduleInfo.setPort(params.getPort());
        moduleInfo.setVersion(params.getVersion()+1);
        moduleInfo.setGmtModified(new Date());
        moduleInfoMapper.updateModuleInfo(moduleInfo);

        return params;
    }

    public ModuleInfo saveAndFlush(ModuleInfo params) {
        return save(params);
    }


    public ModuleInfo findByAppNameAndIp(String appName, String ip) {
        ModuleInfoQuery query = new ModuleInfoQuery(appName, ip, null);
        List<ModuleInfo> moduleInfos = moduleInfoMapper.queryModuleInfo(
                query.toParams()
        );

        if (CollectionUtils.isEmpty(moduleInfos)) {
            return null;
        }

        return moduleInfos.get(0);
    }

    public void remove(ModuleInfo moduleInfo) {
        if (moduleInfo.getId()!=null) {
            moduleInfoMapper.deleteById(moduleInfo.getId());
        }
    }
}
