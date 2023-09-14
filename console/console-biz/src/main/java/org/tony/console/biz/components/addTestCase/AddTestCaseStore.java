package org.tony.console.biz.components.addTestCase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.components.Order;
import org.tony.console.biz.request.AddTestCaseRequest;
import org.tony.console.common.domain.RecordType;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.mongo.RecordMongoService;
import org.tony.console.mongo.model.RecordMDO;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.model.TestCaseDTO;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2022/12/16 17:20
 */
@Order(10)
@Component
public class AddTestCaseStore implements AddTestCaseBizComponent {

    @Resource
    TestCaseService testCaseService;

    @Resource
    RecordDao recordDao;

    @Resource
    RecordMongoService recordMongoService;

    public void execute(AddTestCaseRequest request) throws BizException {

        List<String> recordIdList = request.getRecordIdList();

        List<RecordMDO> recordMDOList = recordMongoService.queryByIdList(recordIdList);

        if (CollectionUtils.isEmpty(recordMDOList)) {
            return;
        }

        List<String> traceIdList = recordMDOList.stream().map(RecordMDO::getTraceId).collect(Collectors.toList());

        RecordQuery query = new RecordQuery();
        query.setTraceIdList(traceIdList);
        query.setAppName(request.getAppName());
        List<Record> records = recordDao.select(query);

        List<TestCaseDTO> testCaseDTOS = build(request.getCaseName(),request.getSuitId(),records, request.getUser());
        if (CollectionUtils.isEmpty(testCaseDTOS)) {
            return;
        }

        testCaseService.addTestCase(testCaseDTOS);

        records.forEach(item->{
            item.setAdd(1);
        });
        recordDao.updateRecordList(records);
    }

    private List<TestCaseDTO> build(String caseName, Long suitId, List<Record> records, String user) {

        List<TestCaseDTO> list = new LinkedList<>();

        for (Record record : records) {
            //添加过不在添加
            if (record.getAdd()!=0) {
                continue;
            }

            TestCaseDTO testCaseDTO = new TestCaseDTO();
            testCaseDTO.setCaseName(caseName);
            testCaseDTO.setSuitId(suitId);

            //这里塞进去
            testCaseDTO.setRecord(record);

            testCaseDTO.setAppName(record.getAppName());
            testCaseDTO.setEnvironment(record.getEnvironment());
            testCaseDTO.setTraceId(record.getTraceId());
            testCaseDTO.setHost(record.getHost());
            testCaseDTO.setEntranceDesc(record.getEntranceDesc());
            testCaseDTO.setGmtRecord(record.getGmtRecord());
            testCaseDTO.setDelete(false);
            testCaseDTO.setUser(user);
            testCaseDTO.setRecordType(RecordType.getByString(record.getType()));
            list.add(testCaseDTO);
        }

        return list;
    }

    @Override
    public boolean isSupport(AddTestCaseRequest request) {
        return true;
    }
}
