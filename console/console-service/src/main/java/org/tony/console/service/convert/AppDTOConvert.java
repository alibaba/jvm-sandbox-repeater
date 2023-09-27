package org.tony.console.service.convert;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.model.App;
import org.tony.console.service.model.app.AppDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/4/2 10:27
 */
@Component
public class AppDTOConvert implements ModelConverter<AppDTO, App> {

    @Override
    public App convert(AppDTO source) {
        return null;
    }

    @Override
    public List<App> convert(List<AppDTO> appDTOS) {
        return null;
    }

    @Override
    public List<AppDTO> reconvertList(List<App> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList<>(0);
        }

        return sList.stream().map(this::reconvert).collect(Collectors.toList());
    }

    @Override
    public AppDTO reconvert(App target) {
        AppDTO appDTO = new AppDTO();
        appDTO.setAppId(target.getAppId());
        appDTO.setBuId(target.getBuId());
        appDTO.setName(target.getName());
        appDTO.setId(target.getId());
        appDTO.setGmtCreate(target.getGmtCreate());

        return appDTO;
    }
}
