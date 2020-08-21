package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.ModuleStatus;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;
import com.alibaba.repeater.console.common.params.ModuleInfoParams;
import com.alibaba.repeater.console.dal.dao.ModuleConfigDao;
import com.alibaba.repeater.console.dal.model.App;
import com.alibaba.repeater.console.dal.model.ModuleConfig;
import com.alibaba.repeater.console.dal.model.ModuleInfo;
import com.alibaba.repeater.console.dal.repository.AppRepository;
import com.alibaba.repeater.console.dal.repository.ModuleConfigRepository;
import com.alibaba.repeater.console.dal.repository.ModuleInfoRepository;
import com.alibaba.repeater.console.service.ModuleConfigService;
import com.alibaba.repeater.console.service.ModuleInfoService;
import com.alibaba.repeater.console.service.convert.ModuleConfigConverter;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import com.alibaba.repeater.console.service.util.ResultHelper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("moduleConfigService")
public class ModuleConfigServiceImpl implements ModuleConfigService {

    @Resource
    private ModuleConfigDao moduleConfigDao;
    @Resource
    private ModuleConfigRepository moduleConfigRepository;
    @Resource
    private ModuleConfigConverter moduleConfigConverter;
    @Resource
    private ModuleInfoService moduleInfoService;
    @Resource
    private AppRepository appRepository;

    @Resource
    private ModuleInfoRepository moduleInfoRepository;

    @Value("${repeat.config.url}")
    private String configURL;

    public List<ModuleConfigBO> list(Long appId) {
        List<ModuleConfig> moduleConfigList = moduleConfigRepository.findByAppId(appId);
        return moduleConfigList.stream().map(moduleConfigConverter::convert).collect(Collectors.toList());
    }

    public void delete(Long id) {
        ModuleConfig moduleConfig = moduleConfigRepository.getOne(id);
        moduleInfoRepository.deleteByModuleConfigId(moduleConfig.getId());
        moduleConfigRepository.delete(id);
    }

    public void update(Long id, String environment, String config, Long appId) {
        ModuleConfig moduleConfig = null;
        if(id != null) {
            moduleConfig = moduleConfigRepository.getOne(id);
        } else {
            moduleConfig = new ModuleConfig();
            App app = appRepository.getOne(appId);
            moduleConfig.setApp(app);
            moduleConfig.setGmtCreate(new Date());
        }
        moduleConfig.setEnvironment(environment);
        moduleConfig.setConfig(config);
        moduleConfig.setGmtModified(new Date());

        moduleConfigRepository.save(moduleConfig);
    }

    @Override
    public RepeaterResult<ModuleConfigBO> query(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(params);
        if (moduleConfig == null) {
            return ResultHelper.fail("data not exist");
        }
        return ResultHelper.success(moduleConfigConverter.convert(moduleConfig));
    }

    @Override
    public RepeaterResult<ModuleConfigBO> saveOrUpdate(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(params);
        if (moduleConfig != null) {
            moduleConfig.setConfig(params.getConfig());
            moduleConfig.setGmtModified(new Date());
        } else {
            moduleConfig = new ModuleConfig();
//            moduleConfig.setAppName(params.getAppName());
            moduleConfig.setEnvironment(params.getEnvironment());
            moduleConfig.setConfig(params.getConfig());
            moduleConfig.setGmtCreate(new Date());
            moduleConfig.setGmtModified(new Date());
        }
        ModuleConfig callback = moduleConfigDao.saveOrUpdate(moduleConfig);
        return ResultHelper.success(moduleConfigConverter.convert(callback));
    }

    @Override
    public RepeaterResult<ModuleConfigBO> push(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(params);
        if (moduleConfig == null) {
            return ResultHelper.fail("config not exist");
        }
        ModuleInfoParams moduleInfoParams = new ModuleInfoParams();
        moduleInfoParams.setAppName(params.getAppName());
        moduleInfoParams.setEnvironment(params.getEnvironment());
        // a temporary size set
        moduleInfoParams.setSize(1000);
        PageResult<ModuleInfoBO> result = null; //FIXME moduleInfoService.query(moduleInfoParams);
        if (result.getCount() == 0) {
            return ResultHelper.fail("no alive module, don't need to push config.");
        }
        String data;
        try {
            RepeaterConfig config = JacksonUtil.deserialize(moduleConfig.getConfig(),RepeaterConfig.class);
            data = SerializerWrapper.hessianSerialize(config);
        } catch (SerializeException e) {
            return ResultHelper.fail("serialize config occurred error, message = " + e.getMessage());
        }
        final Map<String,String> paramMap = new HashMap<>(2);
        paramMap.put(Constants.DATA_TRANSPORT_IDENTIFY,  URLEncoder.encode(data));
        final Map<String,HttpUtil.Resp> respMap = Maps.newHashMap();
        result.getData().forEach(module -> {
            HttpUtil.Resp resp = HttpUtil.doGet(String.format(configURL, module.getIp(), module.getPort()), paramMap);
            respMap.put(module.getIp(), resp);
        });
        String ips = respMap.entrySet().stream().filter(entry -> !entry.getValue().isSuccess()).map(Map.Entry::getKey).collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(ips)) {
            return ResultHelper.success(ips + " push failed.");
        }
        return ResultHelper.success();
    }
}
