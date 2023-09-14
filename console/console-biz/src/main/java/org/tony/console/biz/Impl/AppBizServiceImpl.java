package org.tony.console.biz.Impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.AppBizService;
import org.tony.console.service.AppService;
import org.tony.console.service.config.BizConfig;
import org.tony.console.biz.model.TestSetConfig;
import org.tony.console.biz.request.app.AddAppRequest;
import org.tony.console.biz.request.app.UpdateAdminRequest;
import org.tony.console.biz.request.app.UpdateDailyTestRequest;
import org.tony.console.biz.request.app.UpdateTestSetRequest;
import org.tony.console.common.enums.Status;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.mapper.AppMapper;
import org.tony.console.db.model.App;
import org.tony.console.db.model.Bu;
import org.tony.console.db.query.TestSuitQuery;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.TestSuitService;
import org.tony.console.service.model.TestSuitDTO;
import org.tony.console.service.model.app.AppDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.enums.ResourceType;
import org.tony.console.service.model.enums.Role;
import org.tony.console.service.model.query.UserResourceQuery;
import org.tony.console.service.UserResourceService;
import org.tony.console.service.model.UserResourceDTO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:43
 */
@Component
public class AppBizServiceImpl implements AppBizService {

    @Resource
    UserResourceService userResourceService;

    @Resource
    AppMapper appMapper;

    @Resource
    BizConfig bizConfig;

    @Resource
    AppConfigService appConfigService;

    @Resource
    TestSuitService testSuitService;

    @Resource
    AppService appService;

    @Override
    public List<App> queryMyApp(String user) {

        //超管返回所有应用
        if (bizConfig.isSuperUser(user)) {
            return appMapper.searchAppList(null);
        }

        UserResourceQuery query = new UserResourceQuery();
        query.setUser(user);
        query.setStatus(Status.VALID);
        query.setType(ResourceType.APP);

        List<UserResourceDTO> userResourceDTOList = userResourceService.search(query);
        if (CollectionUtils.isEmpty(userResourceDTOList)) {
            return new ArrayList<>(0);
        }

        List<Long> appIdList = userResourceDTOList.stream().map(UserResourceDTO::getRid).collect(Collectors.toList());
        return appMapper.queryByIdList(appIdList);
    }

    @Override
    public List<Bu> queryBu() {

        return appMapper.queryBuList();
    }

    @Override
    public void addApp(AddAppRequest request) throws BizException {
        request.check();

        App exit = appMapper.selectByName(request.getAppName());
        if (exit!=null) {
            throw BizException.build("已存在，无需重新添加");
        }

        App app = new App();
        app.setAppId(request.getAppId());
        app.setName(request.getAppName());
        app.setBuId(request.getBuId());
        appMapper.insert(app);

        List<UserResourceDTO> addList = new LinkedList<>();

        //先找需要删除的
        for (String user : request.getAdmins()) {

            UserResourceDTO resourceDTO = new UserResourceDTO();
            resourceDTO.setRid(app.getId());
            resourceDTO.setRole(Role.ADMIN);
            resourceDTO.setUser(user.trim());
            resourceDTO.setResourceType(ResourceType.APP);
            resourceDTO.setStatus(Status.VALID);
            resourceDTO.setName(request.getAppName());

            addList.add(resourceDTO);
        }

        if (!CollectionUtils.isEmpty(addList)) {
            userResourceService.add(addList);
        }
    }

    @Override
    public void updateAdmin(UpdateAdminRequest request) throws BizException {
        request.check();
        App app = appMapper.selectByName(request.getAppName());
        if (app == null) {
            throw BizException.build("app is not exist");
        }

        UserResourceQuery query = new UserResourceQuery();
        query.setName(request.getAppName());
        query.setStatus(Status.VALID);
        query.setType(ResourceType.APP);

        //查询出所有的人
        List<UserResourceDTO> userResourceDTOList = checkAuth(request.getAppName(), request.getOperator());

        List<UserResourceDTO> removeList = new LinkedList<>();
        List<UserResourceDTO> addList = new LinkedList<>();

        //先找需要删除的
        for (String user : request.getAdmins()) {
            if (userResourceDTOList.stream().noneMatch(item -> item.getUser().equalsIgnoreCase(user.trim()))) {

                UserResourceDTO resourceDTO = new UserResourceDTO();
                resourceDTO.setRid(app.getId());
                resourceDTO.setRole(Role.ADMIN);
                resourceDTO.setUser(user.trim());
                resourceDTO.setResourceType(ResourceType.APP);
                resourceDTO.setStatus(Status.VALID);
                resourceDTO.setName(request.getAppName());

                addList.add(resourceDTO);
            }
        }

        for (UserResourceDTO u : userResourceDTOList) {
            if (!request.getAdmins().contains(u.getUser())) {
                removeList.add(u);
            }
        }

        if (!CollectionUtils.isEmpty(addList)) {
            userResourceService.add(addList);
        }

        if (!CollectionUtils.isEmpty(removeList)) {
            userResourceService.remove(removeList);
        }
    }

    @Override
    public void updateBu(String appName, Integer buId) {
        App app = appMapper.selectByName(appName);
        if (app == null) {
            return;
        }

        if (app.getBuId().equals(buId)) {
            return;
        }

        app.setBuId(buId);
        appMapper.update(app);
    }

    @Override
    public void updateTestTaskSet(UpdateTestSetRequest request) throws BizException {
        request.check();
        checkAuth(request.getAppName(), request.getOperator());

        appConfigService.saveTestTaskSet(
                request.getAppName(),
                Env.fromString(request.getEnv()),
                request.getAppTestTaskSet()
        );

    }

    @Override
    public void updateDailyTest(UpdateDailyTestRequest request) throws BizException {
        request.check();
        checkAuth(request.getAppName(), request.getOperator());

        appConfigService.setDailyTest(request.getAppName(), request.getAppDailyTest());
    }

    @Override
    public TestSetConfig queryTestTaskSet(String appName, Env env) {

        AppTestTaskSetDTO appTestTaskSetDTO = appConfigService.queryTestTaskSet(appName, env);

        Set<Long> idSet = appTestTaskSetDTO.getTaskIdSet();

        TestSuitQuery testSuitQuery = new TestSuitQuery();
        testSuitQuery.setAppName(appName);
        testSuitQuery.setSuitIdList(Lists.newArrayList(idSet));

        List<TestSuitDTO> testSuitDTOList = testSuitService.search(testSuitQuery);

        TestSetConfig testSetConfig = new TestSetConfig();
        testSetConfig.setTestSuitDTOList(testSuitDTOList);
        testSetConfig.setOpen(appTestTaskSetDTO.getOpen());
        testSetConfig.setFailRetryTime(appTestTaskSetDTO.getFailRetryTime());

        testSetConfig.setFeishuWebHooks(appTestTaskSetDTO.getFeishuWebHooks());

        return testSetConfig;
    }

    public List<UserResourceDTO> checkAuth(String appName, String user) throws BizException {

        UserResourceQuery query = new UserResourceQuery();
        query.setName(appName);
        query.setStatus(Status.VALID);
        query.setType(ResourceType.APP);

        //查询出所有的人
        List<UserResourceDTO> userResourceDTOList = userResourceService.search(query);
        if (userResourceDTOList.stream().anyMatch(item -> item.getUser().equalsIgnoreCase(user))) {
            return userResourceDTOList;
        }

        if (bizConfig.isSuperUser(user)) {
            return userResourceDTOList;
        }

        throw BizException.build("非管理员，没有该应用的操作权限");
    }

    @Override
    public AppDTO queryApp(String appName) {
        return appService.queryApp(appName);
    }
}
