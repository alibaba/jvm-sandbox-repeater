package com.alibaba.repeater.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.dal.model.ModuleConfig;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * {@link ModuleConfigConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("moduleConfigConverter")
@Slf4j
public class ModuleConfigConverter implements ModelConverter<ModuleConfig, ModuleConfigBO> {

    @Override
    public ModuleConfigBO convert(ModuleConfig source) {
        ModuleConfigBO bo = new ModuleConfigBO();
        BeanUtils.copyProperties(source, bo);
        try {
            bo.setConfigModel(JacksonUtil.deserialize(source.getConfig(), RepeaterConfig.class));
        } catch (SerializeException e) {
            log.error("error occurred when deserialize module config", e);
        }
        return bo;
    }

    @Override
    public ModuleConfig reconvert(ModuleConfigBO target) {
        return null;
    }
}
