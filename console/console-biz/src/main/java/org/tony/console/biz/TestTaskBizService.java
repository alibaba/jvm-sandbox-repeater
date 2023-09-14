package org.tony.console.biz;

import org.tony.console.biz.model.TaskVO;
import org.tony.console.biz.model.TestCaseExecResultVO;
import org.tony.console.biz.request.CreateTestTaskBizRequest;
import org.tony.console.biz.request.DeployCallbackRequest;
import org.tony.console.biz.request.RunTestTaskFailBizRequest;
import org.tony.console.biz.request.UpdateTaskItemRequest;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.exception.BizException;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.service.model.TaskItemDTO;


public interface TestTaskBizService {

    /**
     * 创建任务
     * @param createTestTaskBizRequest 创建任务
     * @return
     * @throws BizException
     */
    public Result<Long> createTask(CreateTestTaskBizRequest createTestTaskBizRequest) throws BizException;

    /**
     * 部署成功之后，创建自动化回放任务
     * @param request
     * @return
     */
    public Result<Long> createTaskOfCallBack(DeployCallbackRequest request) throws BizException;

    /**
     * 执行某一个任务
     * @param taskDTO
     * @throws BizException
     */
    public void runTask(TaskDTO taskDTO) throws BizException;

    /**
     * 重跑失败
     * @throws BizException
     */
    public void runTaskFail(RunTestTaskFailBizRequest request) throws BizException;

    public void endTask(TaskDTO taskDTO);

    /**
     * 执行任务
     * @param taskId task任务id
     */
    public void runTaskItem(Long taskId, TaskItemDTO item) throws BizException;

    /**
     * 成功某个子任务
     */
    public void successTaskItem(UpdateTaskItemRequest updateTaskItemRequest);

    /**
     * 失败某个子任务
     */
    public void failTaskItem(UpdateTaskItemRequest updateTaskItemRequest);

    /**
     * 强制失败
     * @param taskId 任务id
     * @param item 任务项
     */
    public void failTaskItem(Long taskId, TaskItemDTO item);

    /**
     * 重试
     * @param item
     */
    public void retryTaskItem(TaskItemDTO item) throws BizException;

    /**
     * 查询测试任务
     * @param query
     * @return
     */
    public PageResult<TaskVO> queryTask(TaskQuery query);

    /**
     * 查询测试任务
     * @param query
     * @return
     */
    public PageResult<TestCaseExecResultVO> queryTaskItem(TaskItemQuery query);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public TaskVO queryById(Long id);
}
