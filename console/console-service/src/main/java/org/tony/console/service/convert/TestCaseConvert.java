package org.tony.console.service.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.RecordType;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.db.model.TestCaseDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2022/12/14 14:25
 */
@Component
public class TestCaseConvert implements ModelConverter<TestCaseDO, TestCaseDTO> {

    private static final String KEY_RECORD_TYPE = "recordType";

    private static final String KEY_SORT = "sort";

    @Override
    public TestCaseDTO convert(TestCaseDO source) {
        if (source == null) {
            return null;
        }

        TestCaseDTO testCaseDTO = new TestCaseDTO();

        testCaseDTO.setCaseId(source.getCaseId());
        testCaseDTO.setCaseName(source.getCaseName());
        testCaseDTO.setId(source.getId());
        testCaseDTO.setSuitId(source.getSuitId());
        testCaseDTO.setExtend(JSON.parseObject(source.getExtend()));
        testCaseDTO.setAppName(source.getAppName());
        testCaseDTO.setTraceId(source.getTraceId());

        testCaseDTO.setHost(source.getHost());
        testCaseDTO.setGmtCreate(source.getGmtCreate());
        testCaseDTO.setEntranceDesc(source.getEntranceDesc());
        testCaseDTO.setEnvironment(source.getEnvironment());
        testCaseDTO.setAppName(source.getAppName());
        testCaseDTO.setGmtRecord(source.getGmtRecord());
        testCaseDTO.setUser(source.getUser());

        if(testCaseDTO.getExtend().containsKey(KEY_RECORD_TYPE)) {
            testCaseDTO.setRecordType(RecordType.getByString(testCaseDTO.getExtend().getString(KEY_RECORD_TYPE)));
        }

        if(testCaseDTO.getExtend().containsKey(KEY_SORT)) {
            testCaseDTO.setSortConfig(
                    testCaseDTO.getExtend().getObject(KEY_SORT, HashMap.class)
            );
        }

        return testCaseDTO;
    }

    @Override
    public List<TestCaseDTO> convert(List<TestCaseDO> testCaseDOS) {
        if (CollectionUtils.isEmpty(testCaseDOS)) {
            return new ArrayList<>(0);
        }

        return testCaseDOS.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<TestCaseDO> reconvertList(List<TestCaseDTO> sList) {
        if (CollectionUtils.isEmpty(sList)) {
            return new ArrayList<>(0);
        }
        return sList.stream().map(this::reconvert).collect(Collectors.toList());
    }

    @Override
    public TestCaseDO reconvert(TestCaseDTO target) {

        TestCaseDO testCaseDO = new TestCaseDO();

        testCaseDO.setCaseId(target.getCaseId());
        testCaseDO.setCaseName(target.getCaseName());

        //类型写进去
        if (target.getRecordType()!=null) {
            target.getExtend().put(KEY_RECORD_TYPE, target.getRecordType().type);
        }

        if (target.getSortConfig()!=null) {
            target.getExtend().put(KEY_SORT, target.getSortConfig());
        }

        testCaseDO.setExtend(JSON.toJSONString(target.getExtend(), SerializerFeature.WriteClassName));
        testCaseDO.setDelete(false);
        testCaseDO.setSuitId(target.getSuitId());
        testCaseDO.setId(target.getId());

        testCaseDO.setEnvironment(target.getEnvironment());
        testCaseDO.setAppName(target.getAppName());
        testCaseDO.setGmtRecord(target.getGmtRecord());
        testCaseDO.setTraceId(target.getTraceId());
        testCaseDO.setGmtCreate(target.getGmtCreate());
        testCaseDO.setEnvironment(target.getEnvironment());
        testCaseDO.setEntranceDesc(target.getEntranceDesc());
        testCaseDO.setHost(target.getHost());
        if (target.getRecord()!=null) {
            testCaseDO.setRecordId(target.getRecord().getId());
        }

        testCaseDO.setUser(target.getUser());

        return testCaseDO;
    }
}
