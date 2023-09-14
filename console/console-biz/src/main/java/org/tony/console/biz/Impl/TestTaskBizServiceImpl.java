package org.tony.console.biz.Impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.tony.console.biz.Constant;
import org.tony.console.biz.FeiShuBizService;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.biz.components.BizFactory;
import org.tony.console.biz.components.BizSession;
import org.tony.console.biz.components.BizTemplate;
import org.tony.console.biz.components.createTask.CreateTaskComponent;
import org.tony.console.biz.components.runTaskItem.RunTaskItemComponent;
import org.tony.console.biz.facade.VenusFacade;
import org.tony.console.biz.facade.model.DeployTask;
import org.tony.console.biz.model.TaskVO;
import org.tony.console.biz.model.TestCaseExecResultVO;
import org.tony.console.biz.model.convert.TaskVOConvert;
import org.tony.console.biz.model.convert.TestCaseExecResultVOConvert;
import org.tony.console.biz.request.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.db.query.TestCaseQuery;
import org.tony.console.service.AppConfigService;
import org.tony.console.service.TaskService;
import org.tony.console.service.TestCaseService;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.service.model.TestCaseDTO;
import org.tony.console.service.model.config.AppTestTaskSetDTO;
import org.tony.console.common.enums.Env;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/5 13:06
 */
@Slf4j
@Component
public class TestTaskBizServiceImpl implements TestTaskBizService {

    @Resource
    BizFactory bizFactory;

    @Resource
    TaskService taskService;

    @Resource
    TestCaseService testCaseService;

    @Resource
    TaskVOConvert taskVOConvert;

    @Resource
    TestCaseExecResultVOConvert testCaseExecResultVOConvert;

    @Value("${task.sync.limit}")
    private int taskSyncLimit;

    @Resource(name="kafkaTaskTemplate")
    private KafkaTemplate<String, String> kafkaTaskTemplate;

    @Value("${kafka.task.topic}")
    private String topic;

    @Resource
    AppConfigService appConfigService;

    @Resource
    VenusFacade venusFacade;

    @Resource
    FeiShuBizService feiShuBizService;

    @Override
    public Result<Long> createTask(CreateTestTaskBizRequest createTestTaskBizRequest) throws BizException {
        createTestTaskBizRequest.check();

        List<Long> taskIdList = new ArrayList<>();
        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(CreateTaskComponent.class, createTestTaskBizRequest);
                taskIdList.add((Long) session.getData(CreateTaskComponent.KEY_TASK_ID));
            }

        }.execute();

        return Result.buildSuccess(taskIdList.get(0), "创建成功");
    }

    @Override
    public Result<Long> createTaskOfCallBack(DeployCallbackRequest request) throws BizException {

        DeployTask deployTask = venusFacade.queryTask(request.getId(), request.getInstanceName());

        String appName = deployTask.getServiceName();
        AppTestTaskSetDTO appTestTaskSetDTO = appConfigService.queryTestTaskSet(appName, Env.fromString(deployTask.getEnvironment()));
        if (!appTestTaskSetDTO.getOpen()) {
            log.warn("app={} env={} 未开启回调自动化", appName, deployTask.getEnvironment());
            return Result.buildSuccess(null, "成功");
        }

        if (CollectionUtils.isEmpty(appTestTaskSetDTO.getTaskIdSet())){
            log.warn("app={} env={} 未配置回归用例集合", appName, deployTask.getEnvironment());
            return Result.buildSuccess(null, "成功");
        }

        CreateTestTaskBizRequest createTestTaskBizRequest = convert(request, deployTask);
        createTestTaskBizRequest.setTestTaskIdList(Lists.newArrayList(appTestTaskSetDTO.getTaskIdSet()));

        List<Long> taskIdList = new ArrayList<>();
        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                session.put(CreateTaskComponent.KEY_DEPLOY_TASK, deployTask);
                bizFactory.execute(CreateTaskComponent.class, createTestTaskBizRequest);
                taskIdList.add((Long) session.getData(CreateTaskComponent.KEY_TASK_ID));
            }

        }.execute();

        log.info("app={} inst={} env={} deploy={} 部署之后创建任务成功 task={}",
                appName,
                deployTask.getInstanceName(),
                deployTask.getEnvironment(),
                deployTask.getId(),
                taskIdList.get(0)
                );

        return Result.buildSuccess(taskIdList.get(0), "创建成功");
    }

    @Override
    public void runTask(TaskDTO taskDTO) throws BizException {
        int flag = taskService.runTask(taskDTO);
        if (flag!=1) {
            return;
        }

        List<Long> taskIdList = taskDTO.getExtend().getJSONArray(Constant.TASK_LIST).toJavaList(Long.class);

        int pageSize = 100;
        int page = 1;
        TestCaseQuery query = new TestCaseQuery();
        query.setAppName(taskDTO.getAppName());

        if (CollectionUtils.isNotEmpty(taskIdList)) {
            query.setSuitIdList(taskIdList);
        }

        Long total = testCaseService.count(query);
        int totalPage = Long.valueOf(total/pageSize).intValue() + 1;

        while (page<=totalPage) {
            query.setPage(page);
            query.setPageSize(pageSize);
            PageResult<TestCaseDTO> pageResult = testCaseService.queryTestCaseWithPage(query);
            List<TestCaseDTO> testCaseDTOList = pageResult.getData();

            List<Long> itemIdList = taskService.addTaskItem(taskDTO.getId(), build(taskDTO, testCaseDTOList));
            for (Long id : itemIdList) {
                 kafkaTaskTemplate.send(topic, id.toString()).addCallback(
                         success->{},
                         failure->{
                             log.error("send task item kafka message error taskItemId={}", id);
                             //如果发送失败，直接改成手动模式
                             TaskItemDTO taskItemDTO = taskService.queryItemById(id);
                             if (!taskItemDTO.getStatus().equals(TaskStatus.INIT)) {
                                 return;
                             }
                             try {
                                 this.runTaskItem(taskItemDTO.getTaskId(), taskItemDTO);
                             } catch (Exception e) {
                                 log.error("system error", e);
                             }
                         }
                 );

            }
            page++;
        }

    }

    @Override
    public void runTaskFail(RunTestTaskFailBizRequest request) throws BizException {
        request.check();

        TaskDTO taskDTO = taskService.queryById(request.getTaskId());
        if (taskDTO == null) {
            throw BizException.build("task不存在");
        }

        if (taskDTO.getStatus().equals(TaskStatus.RUNNING)) {
            throw BizException.build("任务未完结，请等待完结之后再重试");
        }

        int flag = taskService.reRunTask(taskDTO);
        if (flag!=1) {
            return;
        }

        //查询跑失败的case
        TaskItemQuery query = new TaskItemQuery();
        query.setTaskStatus(TaskStatus.FAIL);
        query.setTaskId(taskDTO.getId());


        Long total = taskService.countItem(query);

        if (total == 0) {
            return;
        }

        int pageSize = 100;
        int totalPage = Long.valueOf(total/pageSize).intValue() + 1;
        int page = 1;
        while (page<=totalPage) {
            query.setPage(page);
            query.setPageSize(pageSize);

            List<TaskItemDTO> taskItemDTOList = taskService.queryItem(query);

            for (TaskItemDTO item : taskItemDTOList) {
                kafkaTaskTemplate.send(topic, item.getId().toString());
            }

            page++;
        }


    }

    @Override
    public void endTask(TaskDTO taskDTO) {

        TaskItemQuery query = new TaskItemQuery();
        query.setTaskId(taskDTO.getId());

        query.setTaskStatus(TaskStatus.SUCCESS);
        Long success = taskService.countItem(query);

        query.setTaskStatus(TaskStatus.FAIL);
        Long fail = taskService.countItem(query);

        taskDTO.setSuccess(success.intValue());
        taskDTO.setFail(fail.intValue());
        taskDTO.setRunning(0);
        taskService.successTask(taskDTO);

        feiShuBizService.sendTaskReport(taskDTO.getId());
    }

    @Override
    public void runTaskItem(Long taskId, TaskItemDTO item) throws BizException {

        TaskDTO taskDTO = taskService.queryById(taskId);
        RunTaskItemRequest request = new RunTaskItemRequest();
        request.setTaskDTO(taskDTO);
        request.setTaskItem(item);

        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(RunTaskItemComponent.class, request);
            }

        }.execute();
    }

    @Override
    public void successTaskItem(UpdateTaskItemRequest request) {

        TaskItemDTO taskItemDTO = taskService.queryItemById(request.getTaskItemId());
        taskItemDTO.addExtend(Constant.REPEAT_ID, request.getRepeatId());
        taskItemDTO.addExtend(Constant.REPEAT_COST, request.getCost());

        taskService.successTaskItem(taskItemDTO.getTaskId(), taskItemDTO);
    }

    @Override
    public void failTaskItem(UpdateTaskItemRequest request) {
        TaskItemDTO taskItemDTO = taskService.queryItemById(request.getTaskItemId());
        taskItemDTO.addExtend(Constant.REPEAT_ID, request.getRepeatId());
        taskItemDTO.addExtend(Constant.REPEAT_COST, request.getCost());

        if (taskItemDTO.getExecTime()-1 >= taskItemDTO.getRetryTime()) {
            taskService.failTaskItem(taskItemDTO.getTaskId(), taskItemDTO);
        } else {
            taskService.failWithRetry(taskItemDTO.getTaskId(), taskItemDTO);
        }
    }

    @Override
    public void failTaskItem(Long taskId, TaskItemDTO item) {
        taskService.failTaskItem(item.getTaskId(), item);
    }

    @Override
    public void retryTaskItem(TaskItemDTO item)  throws BizException {

        //有可能任务直接被物理删除了，这里咱就不处理了
        TaskDTO taskDTO = taskService.queryById(item.getTaskId());
        if (taskDTO==null) {
            return;
        }

        RunTaskItemRequest request = new RunTaskItemRequest();
        request.setTaskDTO(taskDTO);
        request.setTaskItem(item);
        request.setRetry(true);

        new BizTemplate() {
            @Override
            public void execute(BizSession session) throws BizException {
                bizFactory.execute(RunTaskItemComponent.class, request);
            }

        }.execute();
    }

    @Override
    public PageResult<TaskVO> queryTask(TaskQuery query) {
        long total = taskService.count(query);

        query.setOrderByGmtCreateDesc(true);
        List<TaskDTO> taskDTOS = taskService.query(query);

        return PageResult.buildSuccess(taskVOConvert.convert(taskDTOS), total);
    }

    @Override
    public PageResult<TestCaseExecResultVO> queryTaskItem(TaskItemQuery query) {

        long total = taskService.countItem(query);
        List<TaskItemDTO> taskItemDTOList = taskService.queryItem(query);

        List<String> caseIdList = taskItemDTOList.stream().map(TaskItemDTO::getName).collect(Collectors.toList());

        TestCaseQuery testCaseQuery = new TestCaseQuery();
        testCaseQuery.setCaseIdList(caseIdList);
        List<TestCaseDTO> testCaseDTOList = testCaseService.queryTestCaseList(testCaseQuery);


        List<TestCaseExecResultVO> voList = testCaseExecResultVOConvert.convert(taskItemDTOList, testCaseDTOList);

        return PageResult.buildSuccess(voList, total);
    }

    @Override
    public TaskVO queryById(Long id) {

        TaskDTO taskDTO = taskService.queryById(id);

        return taskVOConvert.convert(taskDTO);
    }

    private  List<TaskItemDTO> build(TaskDTO taskDTO, List<TestCaseDTO> testCaseDTOList) {
        List<TaskItemDTO> list = new LinkedList<>();

        for (TestCaseDTO caseDTO : testCaseDTOList) {
            TaskItemDTO item = new TaskItemDTO();

            item.setRetryTime(taskDTO.getRetryTime());
            item.setName(caseDTO.getCaseId());
            item.setStatus(TaskStatus.INIT);
            item.setType(taskDTO.getType());
            item.setTaskId(taskDTO.getId());

            list.add(item);
        }

        return list;
    }

    private CreateTestTaskBizRequest convert(DeployCallbackRequest deployCallbackRequest, DeployTask deployTask) {
        CreateTestTaskBizRequest request = new CreateTestTaskBizRequest();

        request.setCreator("SYSTEM");
        request.setAppName(deployTask.getServiceName());
        request.setDeployTaskId(deployCallbackRequest.getId());
        request.setDeployInstName(deployTask.getInstanceName());
        request.setName(String.format("部署任务[部署id=%s]", deployTask.getId()));
        request.setEnvironment(deployTask.getEnvironment());

        return request;
    }
}
