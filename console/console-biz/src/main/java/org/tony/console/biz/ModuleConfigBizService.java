package org.tony.console.biz;

import org.tony.console.biz.model.StaticConfigVO;
import org.tony.console.biz.request.UpdateStaticConfigRequest;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.common.exception.BizException;

/**
 * @author peng.hu1
 * @Date 2022/12/1 16:25
 */
public interface ModuleConfigBizService {

    public void saveOrUpdate(ModuleConfigBO moduleConfigBO) throws BizException;

    public StaticConfigVO getConfig(String appName, String env);

    public void updateStaticConfig(UpdateStaticConfigRequest request) throws BizException;

    public void pushConfigToAgent(String appName, String env, int type) throws BizException;
}
