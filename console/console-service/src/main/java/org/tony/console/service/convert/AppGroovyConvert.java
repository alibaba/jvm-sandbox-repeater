package org.tony.console.service.convert;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.enums.Status;
import org.tony.console.db.model.AppGroovyConfigDO;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.groovy.GroovyConfigDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:34
 */
@Component
public class AppGroovyConvert implements ModelConverter<AppGroovyConfigDO, GroovyConfigDTO> {

    @Override
    public GroovyConfigDTO convert(AppGroovyConfigDO source) {
        if (source == null) {
            return null;
        }

        GroovyConfigDTO configDTO = new GroovyConfigDTO();

        configDTO.setContent(source.getContent());
        configDTO.setGroovyType(GroovyType.valueOf(source.getType()));
        configDTO.setAppName(source.getAppName());
        configDTO.setGmtCreate(source.getGmtCreate());
        configDTO.setId(source.getId());
        configDTO.setStatus(Status.getByCode(source.getStatus()));
        configDTO.setGmtUpdate(source.getGmtUpdate());
        configDTO.setVersion(source.getVersion());
        configDTO.setUser(source.getUser());
        configDTO.setName(source.getName());

        if (!StringUtils.isEmpty(source.getEnv())) {
            String[] envs = source.getEnv().split(",");

            List<Env> envList = new LinkedList<>();
            for (String env : envs) {
                envList.add(Env.fromString(env));
            }

            configDTO.setEnvList(envList);
        } else {
            configDTO.setEnvList(new ArrayList<>(0));
        }

        return configDTO;
    }

    @Override
    public List<GroovyConfigDTO> convert(List<AppGroovyConfigDO> appGroovyConfigDOS) {
        if (CollectionUtils.isEmpty(appGroovyConfigDOS)) {
            return new ArrayList<>(0);
        }

        return appGroovyConfigDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<AppGroovyConfigDO> reconvertList(List<GroovyConfigDTO> sList) {
        return null;
    }

    @Override
    public AppGroovyConfigDO reconvert(GroovyConfigDTO target) {
        AppGroovyConfigDO ado = new AppGroovyConfigDO();
        ado.setId(target.getId());
        ado.setAppName(target.getAppName());
        ado.setContent(target.getContent());
        ado.setGmtCreate(target.getGmtCreate());
        ado.setName(target.getName());
        ado.setStatus(target.getStatus().code);
        ado.setType(target.getGroovyType().name());
        ado.setVersion(target.getVersion());
        ado.setUser(target.getUser());

        if (!CollectionUtils.isEmpty(target.getEnvList())) {
            ado.setEnv(StringUtils.join(target.getEnvList(), ","));
        }

        return ado;
    }
}
