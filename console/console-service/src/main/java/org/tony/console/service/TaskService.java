package org.tony.console.service;

import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.service.model.TaskItemDTO;

import java.util.List;

public interface TaskService {

    /**
     * 创建测试任务
     * @param taskDTO 测试任务
     */
    public Long createTask(TaskDTO taskDTO);

    /**
     * 添加测试任务
     * @param taskItemDTOList
     * @return
     */
    public List<Long> addTaskItem(Long taskId, List<TaskItemDTO> taskItemDTOList);


    /**
     * 查询任务
     * @param taskQuery
     * @return
     */
    public List<TaskDTO> query(TaskQuery taskQuery);

    /**
     * 查询
     * @param query
     * @return
     */
    public List<TaskItemDTO> queryItem(TaskItemQuery query);

    public Long countItem(TaskItemQuery query);

    /**
     * 计算
     * @param taskQuery
     * @return
     */
    public Long count(TaskQuery taskQuery);

    /**
     * 根据id查询task信息
     * @param taskId
     * @return
     */
    public TaskDTO queryById(Long taskId);

    /**
     * 根据幂等字段查询
     * @param appName
     * @param bizId
     * @return
     */
    public TaskDTO queryByBizId(String appName, String bizId);

    /**
     * 查询任务子项
     * @param id
     * @return
     */
    public TaskItemDTO queryItemById(Long id);

    /**
     * 更新task为run
     * @param
     * @return
     */
    public int runTask(TaskDTO taskDTO);

    /**
     * 重跑
     * @param taskDTO 参数
     * @return
     */
    public int reRunTask(TaskDTO taskDTO);

    /**
     * 直接将task置为失败
     * @param taskDTO
     * @return
     */
    public int failTask(TaskDTO taskDTO);


    public int successTask(TaskDTO taskDTO);

    /**
     * pao
     * @param
     * @return
     */
    public int runTaskItem(Long taskId, TaskItemDTO item);

    /**
     * 失败
     * @param
     * @return
     */
    public int failTaskItem(Long taskId, TaskItemDTO item);

    /**
     * 失败重试
     * @return
     */
    public int failWithRetry(Long taskId, TaskItemDTO item);

    /**
     * 成功
     * @param taskId
     * @param
     * @return
     */
    public int successTaskItem(Long taskId, TaskItemDTO item);

    /**
     * 删除测试任务子项
     * @param taskId
     * @param items
     * @return
     */
    public int removeTaskItem(Long taskId, List<TaskItemDTO> items);
}
