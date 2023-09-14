package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.jvm.sandbox.repeater.plugin.diff.DifferenceDO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@link DifferenceConvert}
 * <p>
 *
 * @author zhaoyb1990
 */
@Component("differenceConvert")
public class DifferenceConvert implements ModelConverter<Difference, DifferenceDO> {

    @Override
    public DifferenceDO convert(Difference source) {
        DifferenceDO differenceDO = new DifferenceDO();
        differenceDO.setNodeName(source.getNodeName());
        differenceDO.setType(source.getType().getReason());
        differenceDO.setActual(JSON.toJSONString(source.getLeft()));
        differenceDO.setExpect(JSON.toJSONString(source.getRight()));
        return differenceDO;
    }

    @Override
    public List<DifferenceDO> convert(List<Difference> differences) {
        return null;
    }

    @Override
    public List<Difference> reconvertList(List<DifferenceDO> sList) {
        return null;
    }

    @Override
    public Difference reconvert(DifferenceDO target) {
        return null;
    }
}
