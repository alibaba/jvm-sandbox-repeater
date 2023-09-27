package org.tony.console.biz.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.RecordWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.biz.AppBizService;
import org.tony.console.biz.TestSuitBizService;
import org.tony.console.biz.components.BizFactory;
import org.tony.console.biz.components.BizSession;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.addTestCase.AddTestCaseBizComponent;
import org.tony.console.biz.model.TestSuitTreeVO;
import org.tony.console.biz.request.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.InvocationBO;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.domain.RecordType;
import org.tony.console.common.enums.Status;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.model.Record;
import org.tony.console.db.query.TestCaseQuery;
import org.tony.console.db.query.TestSuitQuery;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.TestSuitService;
import org.tony.console.service.convert.ModelConverter;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.TestCaseDetailDTO;
import org.tony.console.service.model.TestSuitDTO;
import org.tony.console.service.model.enums.TestSuitType;
import org.tony.console.service.utils.JacksonUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2022/12/15 21:18
 */
@Slf4j
@Component
public class TestSuitBizServiceImpl  implements TestSuitBizService {

    @Resource
    TestSuitService testSuitService;

    @Resource
    TestCaseService testCaseService;

    @Resource
    BizFactory bizFactory;

    @Resource
    AppBizService appBizService;

    @Resource
    private ModelConverter<Invocation, InvocationBO> invocationConverter;

    @Override
    public Result addTestSuit(AddTestSuitBizRequest request) throws BizException {
        request.check();
        appBizService.checkAuth(request.getAppName(), request.getUser());
        TestSuitDTO testSuitDTO = build(request);
        testSuitService.addTestSuit(testSuitDTO);
        return Result.buildSuccess(testSuitDTO, "添加成功");
    }

    @Override
    public Result removeTestSuit(RemoveTestSuitRequest request) throws BizException {
        request.check();
        TestSuitDTO suitDTO = testSuitService.queryById(request.getSuitId());

        //任务的删除
        if (TestSuitType.Task.equals(suitDTO.getType())) {
            testCaseService.removeTestCaseOfSuit(suitDTO.getId());
        }

        testSuitService.removeSuit(request.getSuitId());

        return Result.buildSuccess(null, "删除成功");
    }

    @Override
    public Result<List<TestSuitTreeVO>> queryTestSuitTree(String appName) {

        TestSuitTreeVO root = new TestSuitTreeVO();
        root.setKey(0L);
        root.setTitle("全部用例");

        List<TestSuitDTO> suitDTOList =  testSuitService.queryAll(appName);
        if (CollectionUtils.isEmpty(suitDTOList)) {
            return Result.buildSuccess(Arrays.asList(root), "查询成功");
        }

        //先按照排序
        suitDTOList.sort(Comparator.comparing(TestSuitDTO::getParentId));

        Map<Long, TestSuitTreeVO> id2TreeNodeMap = new HashMap<>();
        id2TreeNodeMap.put(root.getKey(), root);

        List<TestSuitTreeVO> testSuitTreeVOS = new LinkedList<>();
        for (int i = 0; i < suitDTOList.size(); i++) {
            TestSuitDTO suitDTO = suitDTOList.get(i);
            TestSuitTreeVO node = build(suitDTO);

            if (id2TreeNodeMap.containsKey(suitDTO.getParentId())) {
                TestSuitTreeVO parent = id2TreeNodeMap.get(suitDTO.getParentId());
                parent.addChild(node);
            } else {
                testSuitTreeVOS.add(node);
            }
            id2TreeNodeMap.put(node.getKey(), node);
        }

        //方便垃圾回收
        id2TreeNodeMap = null;
        return Result.buildSuccess(Arrays.asList(root), "查询成功");
    }

    @Override
    public Result<List<TestSuitDTO>> searchSuitTask(String appName, String key) {

        TestSuitQuery query = new TestSuitQuery();
        query.setStatus(Status.VALID);
        query.setType(TestSuitType.Task.code);
        query.setAppName(appName);

        if (StringUtils.isNotEmpty(key)) {
            query.setName(key);
        }

        List<TestSuitDTO> suitDTOList = testSuitService.search(query);

        return Result.buildSuccess(suitDTOList, "查询成功");
    }

    @Override
    public Result addTestCase(AddTestCaseRequest request) throws BizException {
        request.check();
        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(AddTestCaseBizComponent.class, request);
            }

        }.execute();

        return Result.buildSuccess(null, "添加成功");
    }

    @Override
    public PageResult<TestCaseDTO> queryTestCase(QueryTestCaseRequest request) {
        TestCaseQuery query = new TestCaseQuery();

        //切记，这里是走索引的
        query.setAppName(request.getAppName());
        query.setSuitIdList(request.getTaskList());
        query.setPage(request.getPage());
        query.setPageSize(request.getPageSize());

        if (StringUtils.isNotEmpty(request.getCaseId())) {
            query.setCaseId(request.getCaseId().trim());
        }

        if (StringUtils.isNotEmpty(request.getCaseName())) {
            query.setCaseName(request.getCaseName().trim());
        }

        if (StringUtils.isNotEmpty(request.getEntranceDesc())) {
            query.setEntrance(request.getEntranceDesc());
        }

        return testCaseService.queryTestCaseWithPage(query);
    }

    @Override
    public Result<TestCaseDetailDTO> queryTestCaseDetail(String caseId) {

        TestCaseDTO testCaseDTO = testCaseService.queryTestCaseDTO(caseId);
        if (testCaseDTO == null) {
            return Result.buildFail("case不存在");
        }

        TestCaseDetailDTO detailDTO = new TestCaseDetailDTO();
        BeanUtils.copyProperties(testCaseDTO, detailDTO);

        Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
        try {
            RecordWrapper wrapper = hessian.deserialize(testCaseDTO.getRecord().getWrapperRecord(), RecordWrapper.class);
            detailDTO.setSubInvocations(
                            Optional.ofNullable(wrapper.getSubInvocations())
                                    .orElse(Collections.emptyList())
                                    .stream().map(invocationConverter::convert)
                                    .collect(Collectors.toList())
            );
        } catch (SerializeException e) {
            log.error("error deserialize record wrapper", e);
        }

        detailDTO.setRequest(testCaseDTO.getRecord().getRequest());
        detailDTO.setResponse(testCaseDTO.getRecord().getResponse());

        Record record = testCaseDTO.getRecord();
        if (RecordType.HTTP.type.equals(record.getType())) {
            List<HashMap> requestObj = JSON.parseArray(record.getRequest(), HashMap.class);
            if (requestObj!=null && requestObj.size()>=1) {
                HashMap r = requestObj.get(0);
                if (r.containsKey("contentType") && r.get("contentType")!=null) {
                    String contentType = (String) r.get("contentType");
                    String body = (String) r.get("body");
                    if (contentType.contains("application/json") && StringUtils.isNotEmpty(body)) {
                        r.put("body", JSON.parseObject(body));
                    }
                }
            }
            detailDTO.setRequestObj(requestObj.toArray());
        }

        //不对外暴露
        detailDTO.setRecord(null);
        return Result.buildSuccess(detailDTO, "查询成功");
    }

    @Override
    public Result addRegression(Long taskId) throws BizException {
        return null;
    }

    @Override
    public Result rmvRegression(Long taskId) throws BizException {
        return null;
    }

    @Override
    public Result moveTestCase(MoveTestCaseBizRequest request) throws BizException {
        request.check();

        TestSuitDTO testSuitDTO = testSuitService.queryById(request.getSuitId());
        if (testSuitDTO==null) {
            throw BizException.build("不存在的TestSuit");
        }

        if (!testSuitDTO.getAppName().equals(request.getAppName())) {
            throw BizException.build("目标测试任务不是本应用的测试任务");
        }

        //校验下权限
        appBizService.checkAuth(request.getAppName(), request.getOperator());
        testCaseService.changeTestCaseSuit(request.getCaseIdList(), request.getSuitId());

        return Result.buildSuccess("操作成功");
    }

    private TestSuitTreeVO build(TestSuitDTO suitDTO) {
        TestSuitTreeVO vo = new TestSuitTreeVO();
        vo.setKey(suitDTO.getId());
        vo.setTitle(suitDTO.getName());
        vo.setRegression(false);
        if (suitDTO.getRegression()!=null) {
            vo.setRegression(suitDTO.getRegression());
        }

        if (suitDTO.getType()!=null && suitDTO.getType().equals(TestSuitType.Task)) {
            vo.setLeaf(true);
        }

        return vo;
    }

    private TestSuitDTO build(AddTestSuitBizRequest addTestSuitBizRequest) {
        TestSuitDTO testSuitDTO = new TestSuitDTO();

        testSuitDTO.setStatus(Status.VALID);
        testSuitDTO.setType(TestSuitType.getByCode(addTestSuitBizRequest.getType()));
        testSuitDTO.setName(addTestSuitBizRequest.getName());
        if (addTestSuitBizRequest.getParentId()<0) {
            testSuitDTO.setParentId(0L);
        } else {
            testSuitDTO.setParentId(addTestSuitBizRequest.getParentId());
        }

        testSuitDTO.setAppName(addTestSuitBizRequest.getAppName());

        return testSuitDTO;
    }


}
