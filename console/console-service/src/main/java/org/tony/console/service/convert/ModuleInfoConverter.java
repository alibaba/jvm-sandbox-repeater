package org.tony.console.service.convert;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.domain.ModuleInfoBO;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.enums.Env;
import org.tony.console.db.model.ModuleInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * {@link ModuleInfoConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("moduleInfoConverter")
public class ModuleInfoConverter implements ModelConverter<ModuleInfo, ModuleInfoBO> {

    @Override
    public ModuleInfoBO convert(ModuleInfo source) {
        ModuleInfoBO moduleInfo = new ModuleInfoBO();
        BeanUtils.copyProperties(source, moduleInfo);
        moduleInfo.setStatus(ModuleStatus.of(source.getStatus()));
        moduleInfo.setEnvironment(Env.fromString(source.getEnvironment().toUpperCase()));
        return moduleInfo;
    }

    @Override
    public List<ModuleInfoBO> convert(List<ModuleInfo> moduleInfos) {
        if (CollectionUtils.isEmpty(moduleInfos)) {
            return new ArrayList<>(0);
        }

        return moduleInfos.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<ModuleInfo> reconvertList(List<ModuleInfoBO> sList) {
        return null;
    }

    @Override
    public ModuleInfo reconvert(ModuleInfoBO target) {
        ModuleInfo moduleInfo = new ModuleInfo();
        BeanUtils.copyProperties(target, moduleInfo);
        moduleInfo.setEnvironment(target.getEnvironment().name().toLowerCase());
        moduleInfo.setStatus(target.getStatus().name());
        return moduleInfo;
    }
}
