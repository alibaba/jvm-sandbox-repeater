package org.tony.console.biz;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyConfig;
import org.tony.console.biz.request.app.SaveGroovyContentRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.service.model.groovy.GroovyConfigDTO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:39
 */
public interface AppConfigBizService {

    public List<GroovyConfig> queryGroovyConfigListForAgent(String appName, String env);

    public GroovyConfig queryGroovyConfigForAgent(String appName, Long id);

    /**
     * 查询groovy配置列表
     * @param appName
     * @return
     */
    public List<GroovyConfigDTO> queryGroovyList(String appName);

    /**
     * 查询细节
     * @param id
     * @return
     */
    public GroovyConfigDTO queryGroovyById(Long id);


    /**
     * 更新groovy的内容
     * @param id
     * @param content
     */
    public void saveGroovyContent(SaveGroovyContentRequest request) throws BizException;
}
