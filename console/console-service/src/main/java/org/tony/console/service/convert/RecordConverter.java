package org.tony.console.service.convert;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.common.domain.RecordBO;
import org.tony.console.common.domain.RecordType;
import org.tony.console.db.model.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        if (source.getAdd() == 1) {
            rb.setAdded(true);
        } else {
            rb.setAdded(false);
        }

        if (StringUtils.isNotEmpty(source.getType())) {
            rb.setRecordType(RecordType.getByString(source.getType()));
        } else {
            if (rb.getEntranceDesc().startsWith("java")){
                rb.setRecordType(RecordType.JAVA);
            } else {
                rb.setRecordType(RecordType.HTTP);
            }
        }

        return rb;
    }

    @Override
    public List<RecordBO> convert(List<Record> records) {
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>(0);
        }

        return records.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<Record> reconvertList(List<RecordBO> sList) {
        return null;
    }

    @Override
    public Record reconvert(RecordBO target) {
        return null;
    }
}
