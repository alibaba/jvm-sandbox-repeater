package org.tony.console.biz.job.clean;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.mapper.TaskMapper;
import org.tony.console.db.model.TaskDO;
import org.tony.console.db.query.TaskQuery;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/4/8 11:44
 */
@Component
public class TaskCleanStrategy implements CleanStrategy<TaskDO> {

    /**
     * 任务保留的时间跟replay保持一致即可
     */
    @Value("${replay.idle.day}")
    private Integer taskIdleDay = 30;

    private final Integer batchSize = 10;

    @Resource
    TaskMapper taskMapper;

    @Override
    public List<TaskDO> getData(long startIndex, Integer size) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.setPage(1);
        taskQuery.setPageSize(size);

        return taskMapper.select(taskQuery.toParams());
    }

    @Override
    public boolean needStoreStartIndex() {
        return false;
    }

    @Override
    public Integer batchSize() {
        return batchSize;
    }

    @Override
    public boolean canClean(TaskDO item) {
        if (DateUtil.getDateGapDay(item.getGmtCreate(), new Date())>taskIdleDay) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(List<TaskDO> itemList) {
        for (TaskDO item : itemList) {
            taskMapper.deleteTask(item);
        }
    }

    @Override
    public String getName() {
        return "TASK-CLEAN-TASK";
    }
}
