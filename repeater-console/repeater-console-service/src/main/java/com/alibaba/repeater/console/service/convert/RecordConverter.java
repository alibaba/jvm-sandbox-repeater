package com.alibaba.repeater.console.service.convert;

import com.alibaba.repeater.console.common.domain.RecordBO;
import com.alibaba.repeater.console.dal.model.Record;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * {@link RecordConverter}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("recordConverter")
public class RecordConverter implements ModelConverter<Record, RecordBO> {

    @Override
    public RecordBO convert(Record source) {
        RecordBO rb = new RecordBO();
        // lazy mode , this isn't a correct way to copy properties.
        BeanUtils.copyProperties(source, rb);
        return rb;
    }

    @Override
    public Record reconvert(RecordBO target) {
        return null;
    }
}
