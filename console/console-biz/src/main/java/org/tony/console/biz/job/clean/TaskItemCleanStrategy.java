package org.tony.console.biz.job.clean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.mapper.TaskMapper;
import org.tony.console.db.model.TaskItemDO;
import org.tony.console.db.query.TaskItemQuery;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/4/8 11:55
 */
@Component
public class TaskItemCleanStrategy implements CleanStrategy<TaskItemDO> {

    /**
     * 任务保留的时间跟replay保持一致即可
     */
    @Value("${replay.idle.day}")
    private Integer taskIdleDay = 30;

    private final Integer batchSize = 100;

    @Resource
    TaskMapper taskMapper;

    @Override
    public List<TaskItemDO> getData(long startIndex, Integer size) {
        TaskItemQuery query = new TaskItemQuery();
        query.setPage(1);
        query.setPageSize(size);

        return taskMapper.selectItem(query.toParams());
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
    public boolean canClean(TaskItemDO item) {
        if (DateUtil.getDateGapDay(item.getGmtCreate(), new Date())>taskIdleDay) {
            return true;
        }
        return false;
    }

    @Override
    public void clean(List<TaskItemDO> itemList) {
        for (TaskItemDO item : itemList) {
            taskMapper.deleteTaskItem(item);
        }
    }

    @Override
    public String getName() {
        return "TASK-ITEM-CLEAN-TASK";
    }
}
