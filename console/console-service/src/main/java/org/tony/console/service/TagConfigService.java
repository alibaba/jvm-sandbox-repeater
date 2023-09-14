package org.tony.console.service;

import org.tony.console.service.model.TagConfigDTO;

import java.util.List;

public interface TagConfigService {

    public List<TagConfigDTO> queryByAppName(String appName);

    public TagConfigDTO queryById(Long id);

    public void addTag(TagConfigDTO tagConfigDTO);

    public void update(TagConfigDTO tagConfigDTO);

    public void remove(Long id);
}
