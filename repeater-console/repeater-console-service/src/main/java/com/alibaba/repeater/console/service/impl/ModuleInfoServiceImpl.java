package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.ModuleStatus;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleInfoParams;
import com.alibaba.repeater.console.dal.dao.ModuleInfoDao;
import com.alibaba.repeater.console.dal.model.ModuleInfo;
import com.alibaba.repeater.console.service.ModuleInfoService;
import com.alibaba.repeater.console.service.convert.ModuleInfoConverter;
import com.alibaba.repeater.console.service.util.ResultHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ModuleInfoServiceImpl}
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("heartbeatService")
public class ModuleInfoServiceImpl implements ModuleInfoService {

    private static String activeURI = "http://%s:%s/sandbox/default/module/http/sandbox-module-mgr/active?ids=repeater";

    private static String frozenURI = "http://%s:%s/sandbox/default/module/http/sandbox-module-mgr/frozen?ids=repeater";

    @Value("${repeat.reload.url}")
    private String reloadURI;

    private static String installBash = "sh %s/sandbox/bin/sandbox.sh -p %s -P 8820";

    @Resource
    private ModuleInfoDao moduleInfoDao;

    @Resource
    private ModuleInfoConverter moduleInfoConverter;

    @Override
    public PageResult<ModuleInfoBO> query(ModuleInfoParams params) {
        Page<ModuleInfo> page = moduleInfoDao.selectByParams(params);
        PageResult<ModuleInfoBO> result = new PageResult<>();
        if (page.hasContent()) {
            result.setSuccess(true);
            result.setPageIndex(params.getPage());
            result.setCount(page.getTotalElements());
            result.setPageSize(params.getSize());
            result.setTotalPage(page.getTotalPages());
            result.setData(page.getContent().stream().map(moduleInfoConverter::convert).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public RepeaterResult<List<ModuleInfoBO>> query(String appName) {
        List<ModuleInfo> byAppName = moduleInfoDao.findByAppName(appName);
        if (CollectionUtils.isEmpty(byAppName)) {
            return ResultHelper.fail("data not exist");
        }
        return ResultHelper.success(
                byAppName.stream().map(moduleInfoConverter::convert).collect(Collectors.toList())
        );
    }

    @Override
    public RepeaterResult<ModuleInfoBO> query(String appName, String ip) {
        ModuleInfo moduleInfo = moduleInfoDao.findByAppNameAndIp(appName, ip);
        if (moduleInfo == null) {
            return RepeaterResult.builder().message("data not exist").build();
        }
        return ResultHelper.success(moduleInfoConverter.convert(moduleInfo));
    }

    @Override
    public RepeaterResult<ModuleInfoBO> report(ModuleInfoBO params) {
        ModuleInfo moduleInfo = moduleInfoConverter.reconvert(params);
        moduleInfo.setGmtModified(new Date());
        moduleInfo.setGmtCreate(new Date());
        moduleInfoDao.save(moduleInfo);
        return ResultHelper.success(moduleInfoConverter.convert(moduleInfo));
    }

    @Override
    public RepeaterResult<ModuleInfoBO> active(ModuleInfoParams params) {
        return execute(activeURI, params, ModuleStatus.ACTIVE);
    }

    @Override
    public RepeaterResult<ModuleInfoBO> frozen(ModuleInfoParams params) {
        return execute(frozenURI, params, ModuleStatus.FROZEN);
    }

    @Override
    public RepeaterResult<String> install(ModuleInfoParams params) {
        // this is a fake local implement; must be overwrite in product usage;
        String runtimeBeanName = ManagementFactory.getRuntimeMXBean().getName();
        String pid = runtimeBeanName.split("@")[0];
        BufferedReader input = null;
        BufferedReader error = null;
        PrintWriter output = null;
        try {
            // /Users/tom/sandbox/bin/sandbox.sh
            String[] path = StringUtils.split(System.getProperty("user.dir"), File.separator);
            String userDir = File.separator + path[0] + File.separator + path[1];
            Process process = Runtime.getRuntime().exec(String.format(installBash, userDir, pid));
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line).append("\n");
            }
            while ((line = error.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return ResultHelper.success("operate success", builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (output != null) {
                output.close();
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return ResultHelper.fail();
    }

    @Override
    public RepeaterResult<String> reload(ModuleInfoParams params) {
        ModuleInfo moduleInfo = moduleInfoDao.findByAppNameAndIp(params.getAppName(), params.getIp());
        if (moduleInfo == null) {
            return ResultHelper.fail("data not exist");
        }
        HttpUtil.Resp resp = HttpUtil.doGet(String.format(reloadURI, moduleInfo.getIp(), moduleInfo.getPort()));
        return ResultHelper.fs(resp.isSuccess());
    }

    private RepeaterResult<ModuleInfoBO> execute(String uri, ModuleInfoParams params, ModuleStatus finishStatus) {
        ModuleInfo moduleInfo = moduleInfoDao.findByAppNameAndIp(params.getAppName(), params.getIp());
        if (moduleInfo == null) {
            return ResultHelper.fail("data not exist");
        }
        HttpUtil.Resp resp = HttpUtil.doGet(String.format(uri, moduleInfo.getIp(), moduleInfo.getPort()));
        if (!resp.isSuccess()) {
            return ResultHelper.fail(resp.getMessage());
        }
        moduleInfo.setStatus(finishStatus.name());
        moduleInfo.setGmtModified(new Date());
        moduleInfoDao.saveAndFlush(moduleInfo);
        return ResultHelper.success(moduleInfoConverter.convert(moduleInfo));
    }
}
