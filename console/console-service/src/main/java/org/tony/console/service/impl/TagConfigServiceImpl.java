package org.tony.console.service.impl;

import org.springframework.stereotype.Component;
import org.tony.console.db.mapper.TagConfigMapper;
import org.tony.console.db.model.TagConfigDO;
import org.tony.console.db.query.TagConfigQuery;
import org.tony.console.service.TagConfigService;
import org.tony.console.service.convert.TagConfigConverter;
import org.tony.console.service.model.TagConfigDTO;
import org.tony.console.service.model.enums.TagConfigScope;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:36
 */
@Component
public class TagConfigServiceImpl implements TagConfigService {

    @Resource
    TagConfigMapper tagConfigMapper;

    @Resource
    TagConfigConverter tagConfigConverter;

    @Override
    public List<TagConfigDTO> queryByAppName(String appName) {

        TagConfigQuery query = new TagConfigQuery();
        query.setAppName(appName);

        return tagConfigConverter.convert(tagConfigMapper.select(query.toParams()));
    }

    @Override
    public TagConfigDTO queryById(Long id) {
        TagConfigDO tagConfigDO = tagConfigMapper.selectById(id);

        return tagConfigConverter.convert(tagConfigDO);
    }

    @Override
    public void addTag(TagConfigDTO tagConfigDTO) {
        if (tagConfigDTO.getScope().equals(TagConfigScope.ALL)) {
            tagConfigDTO.setIdentity(null);
        }

        tagConfigMapper.insert(tagConfigConverter.reconvert(tagConfigDTO));
    }

    @Override
    public void update(TagConfigDTO tagConfigDTO) {

        tagConfigMapper.update(tagConfigConverter.reconvert(tagConfigDTO));
    }

    @Override
    public void remove(Long id) {
        tagConfigMapper.delete(id);
    }
}
