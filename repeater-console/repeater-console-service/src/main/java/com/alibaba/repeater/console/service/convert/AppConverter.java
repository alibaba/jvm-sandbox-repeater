package com.alibaba.repeater.console.service.convert;

import com.alibaba.repeater.console.common.domain.AppBO;
import com.alibaba.repeater.console.dal.model.App;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * {@link AppConverter}
 * <p>
 *
 * @author Flag
 */
@Component
@Slf4j
public class AppConverter implements ModelConverter<App, AppBO> {

    @Override
    public AppBO convert(App source) {
        AppBO bo = new AppBO();
        BeanUtils.copyProperties(source, bo);
        return bo;
    }

    @Override
    public App reconvert(AppBO target) {
        return null;
    }
}
