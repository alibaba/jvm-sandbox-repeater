package org.tony.console.service.impl;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.mapper.UserResourceMapper;
import org.tony.console.db.model.UserResourceDO;
import org.tony.console.service.model.query.UserResourceQuery;
import org.tony.console.service.UserResourceService;
import org.tony.console.service.convert.UserResourceConverter;
import org.tony.console.service.model.UserResourceDTO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:37
 */
@Component
public class UserResourceServiceImpl implements UserResourceService {

    @Resource
    UserResourceMapper userResourceMapper;

    @Resource
    UserResourceConverter userResourceConverter;

    @Override
    public int add(List<UserResourceDTO> userResourceDTOList) {

        for (UserResourceDTO dto : userResourceDTOList) {
            userResourceMapper.insert(userResourceConverter.reconvert(dto));
        }
        return userResourceDTOList.size();
    }

    @Override
    public int remove(List<UserResourceDTO> userResourceDTOList) {
        if (CollectionUtils.isEmpty(userResourceDTOList)) {
            return 0;
        }

        List<Long> idList = userResourceDTOList.stream().map(UserResourceDTO::getId).collect(Collectors.toList());
        return userResourceMapper.delete(idList);
    }

    @Override
    public List<UserResourceDTO> search(UserResourceQuery query) {

        List<UserResourceDO> userResourceDOS = userResourceMapper.select(query.toParams());
        return userResourceConverter.convert(userResourceDOS);
    }
}
