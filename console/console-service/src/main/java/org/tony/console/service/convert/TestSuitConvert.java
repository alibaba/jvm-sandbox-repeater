package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.model.TestSuitDO;
import org.tony.console.service.model.TestSuitDTO;
import org.tony.console.service.model.enums.TestSuitType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:14
 */
@Component
public class TestSuitConvert implements ModelConverter<TestSuitDO, TestSuitDTO> {

    public static String KEY_REGRESSION = "regression";

    @Override
    public TestSuitDTO convert(TestSuitDO source) {
        if (source == null) {
            return null;
        }

        TestSuitDTO suitDTO = new TestSuitDTO();
        suitDTO.setId(source.getId());
        suitDTO.setExtend(JSON.parseObject(source.getExtend()));

        if (suitDTO.getExtend().containsKey(KEY_REGRESSION)) {
            suitDTO.setRegression(true);
        }

        suitDTO.setType(TestSuitType.getByCode(source.getType()));
        suitDTO.setParentId(source.getParentId());
        suitDTO.setName(source.getName());
        suitDTO.setGmtCreate(source.getGmtCreate());
        suitDTO.setGmtUpdate(source.getGmtUpdate());
        suitDTO.setId(suitDTO.getId());
        suitDTO.setAppName(source.getAppName());
        return suitDTO;
    }

    @Override
    public List<TestSuitDTO> convert(List<TestSuitDO> testSuitDOS) {
        if (CollectionUtils.isEmpty(testSuitDOS)) {
            return new ArrayList<>(0);
        }

        return testSuitDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TestSuitDO> reconvertList(List<TestSuitDTO> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList<>(0);
        }

        return sList.stream().map(this::reconvert).collect(Collectors.toList());
    }

    @Override
    public TestSuitDO reconvert(TestSuitDTO target) {
        TestSuitDO suitDO = new TestSuitDO();

        if (target.getExtend() == null) {
            target.setExtend(new JSONObject());
        }

        if (target.getRegression()!=null) {
            target.getExtend().put(KEY_REGRESSION, "1");
        }

        suitDO.setExtend(target.getExtend().toString());

        suitDO.setParentId(target.getParentId());
        suitDO.setId(target.getId());
        suitDO.setType(target.getType().code);
        suitDO.setGmtUpdate(target.getGmtUpdate());
        suitDO.setGmtCreate(target.getGmtCreate());
        suitDO.setStatus(target.getStatus().code);
        suitDO.setAppName(target.getAppName());
        suitDO.setName(target.getName());
        return suitDO;
    }
}
