package org.tony.console.service.impl;

import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.AppGroovyMapper;
import org.tony.console.db.model.AppGroovyConfigDO;
import org.tony.console.service.AppGroovyService;
import org.tony.console.service.convert.AppGroovyConvert;
import org.tony.console.service.model.groovy.GroovyConfigDTO;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:29
 */
@Component
public class AppGroovyServiceImpl implements AppGroovyService {

    @Resource
    AppGroovyMapper appGroovyMapper;

    @Resource
    AppGroovyConvert appGroovyConvert;

    @Override
    public List<GroovyConfigDTO> queryList(String appName, boolean withContent) {
        List<AppGroovyConfigDO> appGroovyConfigDOS;
        if (withContent) {
            appGroovyConfigDOS = appGroovyMapper.queryByApp(appName);
        } else {
            appGroovyConfigDOS = appGroovyMapper.queryByAppWithoutContent(appName);
        }
        return appGroovyConvert.convert(appGroovyConfigDOS);
    }

    @Override
    public GroovyConfigDTO queryById(Long id) {
        return appGroovyConvert.convert(appGroovyMapper.queryById(id));
    }

    @Override
    public void updateContent(GroovyConfigDTO configDTO) {
        appGroovyMapper.updateContent(appGroovyConvert.reconvert(configDTO));
    }

    @Override
    public void add(GroovyConfigDTO groovyConfigDTO) {
        appGroovyMapper.insert(appGroovyConvert.reconvert(groovyConfigDTO));
    }
}
