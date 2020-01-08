package com.alibaba.repeater.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.repeater.console.common.domain.DifferenceBO;
import org.springframework.stereotype.Component;

/**
 * {@link DifferenceConvert}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("differenceConvert")
public class DifferenceConvert implements ModelConverter<Difference, DifferenceBO> {

    @Override
    public DifferenceBO convert(Difference source) {
        DifferenceBO bo = new DifferenceBO();
        bo.setNodeName(source.getNodeName());
        bo.setType(source.getType().getReason());
        bo.setActual(JSON.toJSONString(source.getLeft()));
        bo.setExpect(JSON.toJSONString(source.getRight()));
        return bo;
    }

    @Override
    public Difference reconvert(DifferenceBO target) {
        return null;
    }
}
