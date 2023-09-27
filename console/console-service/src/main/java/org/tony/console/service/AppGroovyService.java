package org.tony.console.service;

import org.tony.console.service.model.groovy.GroovyConfigDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:25
 */
public interface AppGroovyService {

    public List<GroovyConfigDTO> queryList(String appName, boolean withContent);

    public GroovyConfigDTO queryById(Long id);

    public void updateContent(GroovyConfigDTO configDTO);

    public void add(GroovyConfigDTO groovyConfigDTO);
}
