package org.tony.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.ModuleConfigBO;
import org.tony.console.db.model.ModuleConfig;
import org.tony.console.service.utils.JacksonUtil;

import java.util.List;

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
    public List<ModuleConfigBO> convert(List<ModuleConfig> moduleConfigs) {
        return null;
    }

    @Override
    public List<ModuleConfig> reconvertList(List<ModuleConfigBO> sList) {
        return null;
    }

    @Override
    public ModuleConfig reconvert(ModuleConfigBO target) {
        return null;
    }
}
