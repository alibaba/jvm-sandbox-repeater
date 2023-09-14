package org.tony.console.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.tony.console.common.Result;
import org.tony.console.common.enums.Status;
import org.tony.console.db.mapper.AppMapper;
import org.tony.console.db.model.App;
import org.tony.console.db.model.Bu;
import org.tony.console.service.AppService;
import org.tony.console.service.UserResourceService;
import org.tony.console.service.convert.AppDTOConvert;
import org.tony.console.service.model.UserResourceDTO;
import org.tony.console.service.model.app.AppDTO;
import org.tony.console.service.model.app.AppGroup;
import org.tony.console.service.model.enums.ResourceType;
import org.tony.console.service.model.query.UserResourceQuery;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/30 15:41
 */
@Component
public class AppServiceImpl implements AppService {

    @Resource
    AppMapper appMapper;

    @Resource
    UserResourceService userResourceService;

    @Resource
    AppDTOConvert appDTOConvert;

    @Override
    public List<AppGroup> queryAppGroupList(String name) {


        List<App> appList = appMapper.searchAppList(name);

        Map<Integer, AppGroup> appGroupMap = new HashMap<>();

        for (App app : appList) {
            if (!appGroupMap.containsKey(app.getBuId())) {
                AppGroup appGroup = new AppGroup();
                appGroup.setId(app.getBuId());
                appGroup.setName(getName(app.getBuId()));
                appGroupMap.put(app.getBuId(), appGroup);
            }

            AppGroup appGroup = appGroupMap.get(app.getBuId());
            appGroup.getAppList().add(app);
        }

        return new LinkedList<>(appGroupMap.values());
    }

    @Override
    public List<AppDTO> queryAppGroup(Integer buId) {

        List<App> appList =  appMapper.queryByBuId(buId);
        return appDTOConvert.reconvertList(appList);
    }

    @Override
    public AppDTO queryApp(String appName) {
        App app = appMapper.selectByName(appName);

        UserResourceQuery query = new UserResourceQuery();
        query.setName(appName);
        query.setStatus(Status.VALID);
        query.setType(ResourceType.APP);

        //查询出所有的人
        List<UserResourceDTO> userResourceDTOList = userResourceService.search(query);
        List<String> admins = userResourceDTOList.stream().map(UserResourceDTO::getUser).collect(Collectors.toList());

        //转换
        AppDTO appDTO = appDTOConvert.reconvert(app);
        appDTO.setAdmins(admins);
        appDTO.setBuName(getName(app.getBuId()));

        return appDTO;
    }

    @Cacheable
    public String getName(Integer buId) {
        List<Bu> buList = appMapper.queryBuList();
        for (Bu bu : buList) {
            if (bu.getId().equals(buId)) {
                return bu.getName();
            }
        }

        return "其它";
    }
}
