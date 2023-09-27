package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.enums.Status;
import org.tony.console.db.model.UserResourceDO;
import org.tony.console.service.model.UserResourceDTO;
import org.tony.console.service.model.enums.ResourceType;
import org.tony.console.service.model.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:27
 */
@Component
public class UserResourceConverter implements ModelConverter<UserResourceDO, UserResourceDTO> {

    @Override
    public UserResourceDTO convert(UserResourceDO source) {
        UserResourceDTO dto = new UserResourceDTO();
        dto.setExtend(JSON.parseObject(source.getExtend()));
        dto.setGmtCreate(source.getGmtCreate());
        dto.setGmtUpdate(source.getGmtUpdate());
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setResourceType(ResourceType.getByCode(source.getType()));
        dto.setUser(source.getUser());
        dto.setRid(source.getRid());
        dto.setRole(Role.getByCode(source.getRole()));
        dto.setStatus(Status.getByCode(source.getStatus()));

        return dto;
    }

    @Override
    public List<UserResourceDTO> convert(List<UserResourceDO> userResourceDOS) {
        if (CollectionUtils.isEmpty(userResourceDOS)) {
            return new ArrayList<>(0);
        }

        return userResourceDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<UserResourceDO> reconvertList(List<UserResourceDTO> sList) {
        return null;
    }

    @Override
    public UserResourceDO reconvert(UserResourceDTO target) {
        UserResourceDO udo = new UserResourceDO();

        if (target.getExtend()!=null) {
            udo.setExtend(target.getExtend().toJSONString());
        }

        udo.setType(target.getResourceType().code);
        udo.setGmtCreate(target.getGmtCreate());
        udo.setGmtUpdate(target.getGmtUpdate());
        udo.setName(target.getName());
        udo.setUser(target.getUser());
        udo.setId(target.getId());
        udo.setRid(target.getRid());
        udo.setStatus(target.getStatus().code);
        udo.setRole(target.getRole().code);

        return udo;
    }
}
