package org.tony.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.kafka.KafkaConfig;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.Page;
import org.tony.console.common.Result;
import org.tony.console.common.domain.*;
import org.tony.console.db.dao.ModuleConfigDao;
import org.tony.console.db.model.ModuleConfig;
import org.tony.console.db.query.ModuleConfigQuery;
import org.tony.console.db.query.ModuleInfoQuery;
import org.tony.console.service.ModuleConfigService;
import org.tony.console.service.ModuleInfoService;
import org.tony.console.service.convert.ModuleConfigConverter;
import org.tony.console.service.kafka.KafkaEnvConfig;
import org.tony.console.service.utils.JacksonUtil;
import org.tony.console.service.utils.ResultHelper;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ModuleConfigServiceImpl implements ModuleConfigService {

    @Resource
    ModuleConfigDao moduleConfigDao;

    @Resource
    ModuleConfigConverter moduleConfigConverter;

    @Resource
    ModuleInfoService moduleInfoService;

    @Resource
    KafkaEnvConfig kafkaEnvConfig;

    @Value("${repeat.config.url}")
    private String configURL;

    @Override
    public PageResult<ModuleConfigBO> list(ModuleConfigParams params) {
        PageResult<ModuleConfigBO> result = new PageResult<>();

        ModuleConfigQuery query = convert(params);
        Page<ModuleConfig> page = moduleConfigDao.selectByParams(query);
        if (page.hasContent()) {
            result.setSuccess(true);
            result.setPageIndex(params.getPage());
            result.setCount(page.getTotal());
            result.setPageSize(params.getSize());
            result.setData(page.getData().stream().map(moduleConfigConverter::convert).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public Result<ModuleConfigBO> query(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(convert(params));
        if (moduleConfig == null) {
            return ResultHelper.fail("data not exist");
        }

        ModuleConfigBO configBO = moduleConfigConverter.convert(moduleConfig);

        if (configBO.getConfigModel().getKafkaConfig() == null) {
            KafkaConfig kafkaConfig = new KafkaConfig();
            kafkaConfig.setServer(kafkaEnvConfig.server);
            kafkaConfig.setPassword(kafkaEnvConfig.password);
            kafkaConfig.setUsername(kafkaEnvConfig.username);
            kafkaConfig.setRepeatTopic(kafkaEnvConfig.replayTopic);
            kafkaConfig.setRecordTopic(kafkaEnvConfig.recordTopic);

            configBO.getConfigModel().setKafkaConfig(kafkaConfig);
        }

        return ResultHelper.success(configBO);
    }

    @Override
    public Result<ModuleConfigBO> queryWithDefault(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(convert(params));
        if (moduleConfig == null) {
            ModuleConfigBO configBO = new ModuleConfigBO();
            configBO.setConfigModel(new RepeaterConfig());
            return ResultHelper.success(configBO);
        }

        return ResultHelper.success(moduleConfigConverter.convert(moduleConfig));
    }

    @Override
    public Result<ModuleConfigBO> saveOrUpdate(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(convert(params));
        if (moduleConfig != null) {
            moduleConfig.setConfig(params.getConfig());
            moduleConfig.setGmtModified(new Date());
        } else {
            moduleConfig = new ModuleConfig();
            moduleConfig.setAppName(params.getAppName());
            moduleConfig.setEnvironment(params.getEnvironment());
            moduleConfig.setConfig(params.getConfig());
            moduleConfig.setGmtCreate(new Date());
            moduleConfig.setGmtModified(new Date());
        }
        ModuleConfig callback = moduleConfigDao.saveOrUpdate(moduleConfig);
        return ResultHelper.success(moduleConfigConverter.convert(callback));
    }

    @Override
    public Result<ModuleConfigBO> push(ModuleConfigParams params) {
        ModuleConfig moduleConfig = moduleConfigDao.query(convert(params));
        if (moduleConfig == null) {
            return ResultHelper.fail("config not exist");
        }

        ModuleInfoQuery query = new ModuleInfoQuery();
        query.setPageSize(1000);
        query.setPage(1);
        query.setAppName(params.getAppName());
        query.setEnvironment(params.getEnvironment());

        PageResult<ModuleInfoBO> result = moduleInfoService.query(query);
        if (result == null || !result.isSuccess()) {
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
        final Map<String, HttpUtil.Resp> respMap = Maps.newHashMap();
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


    private ModuleConfigQuery convert(ModuleConfigParams params) {
        ModuleConfigQuery query = new ModuleConfigQuery();

        query.setConfig(params.getConfig());
        query.setPage(params.getPage());
        query.setAppName(params.getAppName());
        query.setPageSize(params.getSize());
        query.setEnvironment(params.getEnvironment());

        return query;
    }
}
