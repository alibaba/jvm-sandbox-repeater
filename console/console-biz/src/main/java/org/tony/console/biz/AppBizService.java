package org.tony.console.biz;

import org.tony.console.biz.model.TestSetConfig;
import org.tony.console.biz.request.app.AddAppRequest;
import org.tony.console.biz.request.app.UpdateAdminRequest;
import org.tony.console.biz.request.app.UpdateDailyTestRequest;
import org.tony.console.biz.request.app.UpdateTestSetRequest;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.App;
import org.tony.console.db.model.Bu;
import org.tony.console.service.model.UserResourceDTO;
import org.tony.console.service.model.app.AppDTO;
import org.tony.console.common.enums.Env;

import java.util.List;

public interface AppBizService {

    /**
     * 查询我的应用
     * @param user
     * @return
     */
    public List<App> queryMyApp(String user);

    /**
     *
     * @return
     */
    public List<Bu> queryBu();

    /**
     * 添加应用
     * @param addAppRequest
     * @throws BizException
     */
    public void addApp(AddAppRequest addAppRequest) throws BizException;

    /**
     * 更新管理员
     * @param request
     * @return
     * @throws BizException
     */
    public void updateAdmin(UpdateAdminRequest request) throws BizException;

    /**
     * 更新bu信息
     * @param appName
     * @param buId
     */
    public void updateBu(String appName, Integer buId);

    /**
     * 更新回归用例集
     * @param request
     * @throws BizException
     */
    public void updateTestTaskSet(UpdateTestSetRequest request) throws BizException;


    public void updateDailyTest(UpdateDailyTestRequest request) throws BizException;

    /**
     * 查询环境配置
     * @param appName 应用名
     * @param env 环境
     * @return
     */
    public TestSetConfig queryTestTaskSet(String appName, Env env);

    /**
     * 权限校验
     * @param appName 应用名称
     * @param user 用户
     * @return
     * @throws BizException
     */
    public List<UserResourceDTO> checkAuth(String appName, String user) throws BizException;

    public AppDTO queryApp(String appName);
}
