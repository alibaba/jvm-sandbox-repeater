package org.tony.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.Page;
import org.tony.console.common.Result;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleInfoParams;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.dao.ModuleInfoDao;
import org.tony.console.db.model.ModuleInfo;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.convert.ModuleInfoConverter;
import org.tony.console.service.utils.ResultHelper;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModuleInfoServiceImpl implements ModuleInfoService {

    private static String flushURI = "http://%s:%s/";

    @Value("${repeat.reload.url}")
    private String reloadURI;

    @Value("${repeat.dynamic.reload.url}")
    private String reloadDynamicURI;

    @Resource
    ModuleInfoDao moduleInfoDao;

    @Resource
    ModuleInfoConverter moduleInfoConverter;

    @Override
    public PageResult<ModuleInfoBO> query(ModuleInfoQuery query) {
        Page<ModuleInfo> moduleInfoPage = moduleInfoDao.selectByParams(query);

        List<ModuleInfo> moduleInfoList = moduleInfoPage.getData();


        PageResult<ModuleInfoBO> pageResult = new PageResult<>();
        pageResult.setPageSize(moduleInfoPage.getPageSize());

        List<ModuleInfoBO> moduleInfoBOList = moduleInfoConverter.convert(moduleInfoList);
        for (ModuleInfoBO moduleInfoBO : moduleInfoBOList) {
            if (
                    ModuleStatus.ACTIVE.equals(moduleInfoBO.getStatus()) &&
                            DateUtil.getDateGapMinNow(moduleInfoBO.getGmtModified())>5)
            {
                moduleInfoBO.setStatus(ModuleStatus.OFFLINE);
            }
        }

        pageResult.setData(moduleInfoBOList);
        pageResult.setSuccess(true);
        pageResult.setCount(moduleInfoPage.getTotal());
        pageResult.setPageIndex(moduleInfoPage.getPageNo());
        return pageResult;
    }

    @Override
    public List<ModuleInfoBO> queryV2(ModuleInfoQuery query) {
        List<ModuleInfo> moduleInfoList = moduleInfoDao.query(query);
        List<ModuleInfoBO> moduleInfoBOList = moduleInfoConverter.convert(moduleInfoList);
        for (ModuleInfoBO moduleInfoBO : moduleInfoBOList) {
            if (
                    ModuleStatus.ACTIVE.equals(moduleInfoBO.getStatus()) &&
                            DateUtil.getDateGapMinNow(moduleInfoBO.getGmtModified())>1)
            {
                moduleInfoBO.setStatus(ModuleStatus.OFFLINE);
            }
        }

        return moduleInfoBOList;
    }

    @Override
    public Result<List<ModuleInfoBO>> query(String appName) {
        List<ModuleInfo> byAppName = moduleInfoDao.findByAppName(appName);
        if (CollectionUtils.isEmpty(byAppName)) {
            return ResultHelper.fail("data not exist");
        }
        return ResultHelper.success(
                byAppName.stream().map(moduleInfoConverter::convert).collect(Collectors.toList())
        );
    }

    @Override
    public Result<ModuleInfoBO> query(String appName, String ip) {
        ModuleInfo moduleInfo = moduleInfoDao.findByAppNameAndIp(appName, ip);
        if (moduleInfo == null) {
            return Result.builder().message("data not exist").build();
        }
        return ResultHelper.success(moduleInfoConverter.convert(moduleInfo));
    }

    @Override
    public Result<ModuleInfoBO> report(ModuleInfoBO params) {
        log.debug("get the module config={}", params);
        ModuleInfo moduleInfo = moduleInfoConverter.reconvert(params);
        moduleInfo.setGmtModified(new Date());
        moduleInfo.setGmtCreate(new Date());
        moduleInfoDao.save(moduleInfo);
        return ResultHelper.success(moduleInfoConverter.convert(moduleInfo));
    }

    @Override
    public Result<ModuleInfoBO> active(ModuleInfoParams params) {
        return null;
    }

    @Override
    public Result<ModuleInfoBO> frozen(ModuleInfoParams params) {
        return null;
    }

    @Override
    public Result<String> install(ModuleInfoParams params) {
        return null;
    }

    @Override
    public Result<String> reload(ModuleInfoParams params) {
        return null;
    }

    @Override
    public Result flush(List<ModuleInfoBO> moduleInfoBOList) {
        if (CollectionUtils.isEmpty(moduleInfoBOList)) {
            return Result.buildSuccess(null, "刷新成功");
        }

        List<ModuleInfo> waitToFlushList = new LinkedList<>();

        for (ModuleInfoBO moduleInfoBO: moduleInfoBOList) {
            ModuleInfo m = moduleInfoDao.findByAppNameAndIp(moduleInfoBO.getAppName(), moduleInfoBO.getIp());
            if (m==null) {
                continue;
            }
            HttpUtil.Resp resp = HttpUtil.doGet(String.format(flushURI, m.getIp(), m.getPort()));
            if (resp.getCode() != 404 && !ModuleStatus.OFFLINE.name().equals(m.getStatus())) {
                m.setStatus(ModuleStatus.OFFLINE.name());
                moduleInfoDao.saveAndFlush(m);
            } else {
                waitToFlushList.add(m);
            }
        }

        for (ModuleInfo m : waitToFlushList) {
            HttpUtil.Resp resp1 = HttpUtil.doGet(String.format(reloadURI, m.getIp(), m.getPort()), 1);
            if (resp1.isSuccess()) {
                log.info("刷新静态配置成功 appName={} ip={} env={}", m.getAppName(), m.getIp(), m.getEnvironment());
            }

            HttpUtil.Resp resp2 = HttpUtil.doGet(String.format(reloadDynamicURI, m.getIp(), m.getPort()), 1);
            if (resp2.isSuccess()) {
                log.info("刷新动态配置成功 appName={} ip={} env={}", m.getAppName(), m.getIp(), m.getEnvironment());
            }

        }

        return Result.buildSuccess(null, "刷新成功");
    }

    @Override
    public Result remove(List<ModuleInfoBO> moduleInfoBOList) {
        if (CollectionUtils.isEmpty(moduleInfoBOList)) {
            return Result.buildSuccess(null, "成功");
        }
        for (ModuleInfoBO moduleInfoBO: moduleInfoBOList) {
            ModuleInfo m = moduleInfoDao.findByAppNameAndIp(moduleInfoBO.getAppName(), moduleInfoBO.getIp());
            if (m==null) {
                continue;
            }
            moduleInfoDao.remove(m);
        }

        return Result.buildSuccess(null, "清理成功");
    }
}
