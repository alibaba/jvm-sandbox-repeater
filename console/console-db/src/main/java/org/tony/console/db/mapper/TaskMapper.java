package org.tony.console.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tony.console.db.model.TaskDO;
import org.tony.console.db.model.TaskItemDO;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {

    /**
     * 插入
     * @param taskDO
     * @return
     */
    public int insert(TaskDO taskDO);

    /**
     * 更新
     * @param taskDO
     * @return
     */
    public int update(TaskDO taskDO);

    /**
     * 查询
     * @param params
     * @return
     */
    public List<TaskDO> select(Map<String, Object> params);

    /**
     * 计数
     * @param params
     * @return
     */
    public long count(Map<String, Object> params);

    /**
     * 查询Item
     * @param params
     * @return
     */
    public List<TaskItemDO> selectItem(Map<String, Object> params);

    /**
     * 计数
     * @param params
     * @return
     */
    public long countItem(Map<String, Object> params);

    /**
     * 查询
     * @return
     */
    public TaskDO selectById(Long id);

    public TaskDO selectByBizId(@Param("appName") String appName, @Param("bizId") String bizId);

    /**
     * 查询单个
     * @param id
     * @return
     */
    public TaskItemDO selectItemById(Long id);

    /**
     * 批量插入
     * @param taskItemDOS
     * @return
     */
    public int batchInsertItem(List<TaskItemDO> taskItemDOS);

    /**
     * 批量更新
     * @param taskItemDOS
     * @return
     */
    public int batchUpdateItem(List<TaskItemDO> taskItemDOS);

    /**
     * 更新
     * @param taskItemDO
     * @return
     */
    public int updateItem(TaskItemDO taskItemDO);


    public int removeTaskItems(@Param("taskId") Long taskId, @Param("idList") List<Long> taskIds);


    public int deleteTask(TaskDO task);

    public int deleteTaskItem(TaskItemDO taskItemDO);
}
