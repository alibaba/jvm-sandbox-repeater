package org.tony.console.biz.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.TestCaseBizService;
import org.tony.console.biz.model.TestCaseCompareSortVO;
import org.tony.console.biz.request.AddTestCaseBizRequest;
import org.tony.console.biz.request.RemoveTestCaseBizRequest;
import org.tony.console.biz.request.ReplaceRespRequest;
import org.tony.console.biz.request.ReplaceSubInvocationRequest;
import org.tony.console.common.Result;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.dao.RecordDao;
import org.tony.console.db.dao.ReplayDao;
import org.tony.console.db.model.Record;
import org.tony.console.db.model.Replay;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.service.CaseConfigService;
import org.tony.console.service.TaskService;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.caseConfig.CaseCompareSortConfig;
import org.tony.console.service.utils.JacksonUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author peng.hu1
 * @Date 2022/12/15 17:52
 */
@Slf4j
@Component
public class TestCaseBizServiceImpl implements TestCaseBizService {

    @Resource
    RecordDao recordDao;

    @Resource
    TestCaseService testCaseService;

    @Resource
    TaskService taskService;

    @Resource
    ReplayDao replayDao;

    @Resource
    CaseConfigService caseConfigService;


    @Override
    public Result add(AddTestCaseBizRequest request) throws BizException {
        request.check();

        RecordQuery query = new RecordQuery();
        query.setRecordIdList(request.getRecordList());
        List<Record> records = recordDao.select(query);

        List<TestCaseDTO> testCaseDTOS = new LinkedList<>();
        for (int i=0; i<records.size(); i++) {
            Record record = records.get(i);

            TestCaseDTO testCaseDTO = new TestCaseDTO();
            testCaseDTO.setRecord(record);
            testCaseDTO.setAppName(record.getAppName());
            testCaseDTO.setSuitId(request.getSuitId());
            testCaseDTO.setHost(record.getHost());
            testCaseDTO.setGmtRecord(record.getGmtRecord());
            testCaseDTO.setExtend(new JSONObject());
            testCaseDTO.setCaseName(request.getCaseName());
            testCaseDTO.setEnvironment(record.getEnvironment());
            testCaseDTO.setEntranceDesc(record.getEntranceDesc());
            testCaseDTO.setDelete(false);

            testCaseDTOS.add(testCaseDTO);
        }

        return Result.buildSuccess(null, "添加成功");
    }

    @Override
    public Result<String> queryTestCaseWrapperRecord(String caseId) {

        Record record = testCaseService.queryTestCaseRecord(caseId);
        if (record!=null) {
            return Result.buildSuccess(record.getWrapperRecord(), "operate success");
        }
        return Result.buildFail("not exist");
    }

    @Override
    public Result removeTestCase(RemoveTestCaseBizRequest request) {

        testCaseService.removeTestCase(request.getCaseIdList());

        if (request.getTaskId()!=null) {
            TaskItemQuery taskItemQuery = new TaskItemQuery();
            taskItemQuery.setNameList(request.getCaseIdList());
            taskItemQuery.setTaskId(request.getTaskId());
            List<TaskItemDTO> taskItemDTOList = taskService.queryItem(taskItemQuery);
            taskService.removeTaskItem(request.getTaskId(), taskItemDTOList);
        }
        return Result.buildSuccess(null, "删除成功");
    }

    @Override
    public Result replaceResponse(ReplaceRespRequest request) throws BizException {
        request.check();

        Record record = testCaseService.queryTestCaseRecord(request.getCaseId());
        Replay replay = replayDao.findByRepeatId(request.getRepeatId());

        record.setResponse(replay.getResponse());

        testCaseService.updateRecord(request.getCaseId(), record);

        return Result.buildSuccess(null, "更新成功");
    }

    @Override
    public Result replaceSubInvocation(ReplaceSubInvocationRequest request) throws BizException {
        request.check();

        Record record = testCaseService.queryTestCaseRecord(request.getCaseId());
        Replay replay = replayDao.findByRepeatId(request.getRepeatId());

        if (record == null) {
            throw BizException.build("record not exist");
        }

        if (replay == null) {
            throw BizException.build("replay not exist");
        }

        try {
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(record.getWrapperRecord(), RecordWrapper.class);
            List<MockInvocation> mockInvocations = JacksonUtil.deserializeArray(replay.getMockInvocation(), MockInvocation.class);

            Optional<MockInvocation> optional = mockInvocations.stream()
                    .filter(item->item.getIndex() == request.getIndex())
                    .filter(item->item.getOriginUri().equals(request.getIdentity()))
                    .findFirst();

            if(optional.isPresent()) {
                MockInvocation mockInvocation = optional.get();
                int originIndex = mockInvocation.getOriginIndex();

                Optional<Invocation> op2 = wrapper.getSubInvocations().stream()
                        .filter(item->item.getIndex() == originIndex)
                        .filter(item->item.getIdentity().getUri().equals(request.getIdentity()))
                        .findFirst();

                if (op2.isPresent()) {
                    Invocation invocation = op2.get();
                    invocation.setRequestSerialized(mockInvocation.getCurrentRequestSerialized());

                    record.setWrapperRecord(SerializerWrapper.hessianSerialize(wrapper));
                    testCaseService.updateRecord(request.getCaseId(), record);
                }


            }


        } catch (SerializeException e) {
            log.error("system error", e);
            throw BizException.build("system error");
        }

        return Result.buildSuccess(null, "更新成功");
    }

    @Override
    public TestCaseCompareSortVO queryCompareSortConfig(String caseId) {

        CaseCompareSortConfig c = caseConfigService.getCompareSortConfig(caseId);

        TestCaseCompareSortVO vo = new TestCaseCompareSortVO();
        if (c==null) {
            vo.setCaseId(caseId);
            vo.setConfigs(new ArrayList<>(0));
            vo.setVersion(0);
            return vo;
        }

        vo.setId(c.getId());
        vo.setVersion(c.getVersion());

        Map<String, String> m = c.getConfig();

        List<TestCaseCompareSortVO.Item> list = new ArrayList<>(m.size());

        for (Map.Entry<String, String> entry : m.entrySet()) {
            TestCaseCompareSortVO.Item i = new TestCaseCompareSortVO.Item();
            i.setKey(entry.getKey());
            i.setField(entry.getValue());
            list.add(i);
        }

        vo.setConfigs(list);

        return vo;
    }

    @Override
    public void updateCompareSortConfig(TestCaseCompareSortVO vo) {

        CaseCompareSortConfig config = new CaseCompareSortConfig();

        config.setId(vo.getId());
        config.setVersion(vo.getVersion());
        config.setCaseId(vo.getCaseId());

        Map<String, String> m = new HashMap<>();

        if (!CollectionUtils.isEmpty(vo.getConfigs())) {
            for (TestCaseCompareSortVO.Item i : vo.getConfigs()) {
                m.put(i.getKey(), i.getField());
            }
        }


        config.setConfig(m);

        caseConfigService.saveCompareSortConfig(config);
    }
}
