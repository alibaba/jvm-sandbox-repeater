package org.tony.console.service.convert;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.model.TagConfigDO;
import org.tony.console.service.model.TagConfigDTO;
import org.tony.console.service.model.enums.TagConfigScope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:37
 */
@Component
public class TagConfigConverter implements ModelConverter<TagConfigDO, TagConfigDTO> {
    @Override
    public TagConfigDTO convert(TagConfigDO source) {
        if (source == null) {
            return null;
        }

        TagConfigDTO configDTO = new TagConfigDTO();
        configDTO.setAppName(source.getAppName());
        configDTO.setId(source.getId());
        configDTO.setJsonpath(source.getJsonpath());
        configDTO.setName(source.getName());
        configDTO.setNickName(source.getNick());
        configDTO.setScope(TagConfigScope.getByCode(source.getScope()));
        configDTO.setIdentity(source.getIdentity());
        return configDTO;
    }

    @Override
    public List<TagConfigDTO> convert(List<TagConfigDO> tagConfigDOS) {
        if (CollectionUtils.isEmpty(tagConfigDOS)) {
            return new ArrayList<>(0);
        }

        return tagConfigDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TagConfigDO> reconvertList(List<TagConfigDTO> sList) {
        return null;
    }

    @Override
    public TagConfigDO reconvert(TagConfigDTO target) {
        TagConfigDO tagConfigDO = new TagConfigDO();

        tagConfigDO.setAppName(target.getAppName());
        tagConfigDO.setJsonpath(target.getJsonpath());
        tagConfigDO.setId(target.getId());
        tagConfigDO.setName(target.getName());
        tagConfigDO.setScope(target.getScope().code);
        tagConfigDO.setIdentity(target.getIdentity());
//        if (target.getIdentity()==null) {
//            tagConfigDO.setIdentity("");
//        }
        tagConfigDO.setNick(target.getNickName());

        return tagConfigDO;
    }
}
