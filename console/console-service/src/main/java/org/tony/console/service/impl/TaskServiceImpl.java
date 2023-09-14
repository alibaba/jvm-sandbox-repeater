package org.tony.console.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.tony.console.db.mapper.TaskMapper;
import org.tony.console.db.model.TaskDO;
import org.tony.console.db.model.TaskItemDO;
import org.tony.console.db.query.TaskItemQuery;
import org.tony.console.db.query.TaskQuery;
import org.tony.console.service.TaskService;
import org.tony.console.service.convert.TaskConvert;
import org.tony.console.service.convert.TaskItemConvert;
import org.tony.console.service.model.TaskDTO;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.service.model.TaskItemDTO;
import org.tony.console.service.redis.RedisUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peng.hu1
 * @Date 2023/1/4 13:46
 */
@Slf4j
@Component
public class TaskServiceImpl implements TaskService {

    @Resource
    TaskMapper taskMapper;

    @Resource
    TaskConvert taskConvert;

    @Resource
    RedisUtil redisUtil;

    @Value("${redis.task.prefix}")
    String redisPrefix;

    @Value("${redis.task.timeout}")
    private Long timeout = 86400L;

    @Resource
    TaskItemConvert taskItemConvert;

    private final static String REDIS_KEY_TOTAL = "total";
    private final static String REDIS_KEY_SUCCESS = "success";
    private final static String REDIS_KEY_RUNNING = "running";
    private final static String REDIS_KEY_FAIL = "fail";

    @Override
    public Long createTask(TaskDTO taskDTO) {

        taskDTO.setStatus(TaskStatus.INIT);
        TaskDO taskDO = taskConvert.reconvert(taskDTO);
        taskMapper.insert(taskDO);
        taskDTO.setId(taskDO.getId());

        return taskDO.getId();
    }

    @Override
    public List<Long> addTaskItem(Long taskId, List<TaskItemDTO> taskItemDTOList) {
        List<TaskItemDO> taskItemDOS = taskItemConvert.reconvertList(taskItemDTOList);

        taskMapper.batchInsertItem(taskItemDOS);

        return taskItemDOS.stream().map(TaskItemDO::getId).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> query(TaskQuery taskQuery) {
        List<TaskDO> taskDOS = taskMapper.select(taskQuery.toParams());
        return taskConvert.convert(taskDOS);
    }

    @Override
    public List<TaskItemDTO> queryItem(TaskItemQuery query) {
        List<TaskItemDO> taskItemDOS = taskMapper.selectItem(query.toParams());
        return taskItemConvert.convert(taskItemDOS);
    }

    @Override
    public Long countItem(TaskItemQuery query) {
        return taskMapper.countItem(query.toParams());
    }


    @Override
    public Long count(TaskQuery taskQuery) {
        return taskMapper.count(taskQuery.toParams());
    }

    @Override
    public TaskDTO queryById(Long taskId) {

        TaskDO taskDO = taskMapper.selectById(taskId);
        if (taskDO == null) {
            return null;
        }

        TaskDTO taskDTO = taskConvert.convert(taskDO);
        String redisKey = getTaskRedisKey(taskDO.getId());

        if (TaskStatus.RUNNING.equals(taskDTO.getStatus())) {
            try {
                String success = redisUtil.get(redisKey+REDIS_KEY_SUCCESS);
                taskDTO.setSuccess(success!=null?Integer.parseInt(success) : 0);

                String fail = redisUtil.get(redisKey+REDIS_KEY_FAIL);
                taskDTO.setFail(fail!=null?Integer.parseInt(fail) : 0);

                String running = redisUtil.get(redisKey+REDIS_KEY_RUNNING);
                taskDTO.setRunning(running!=null?Integer.parseInt(running) : 0);
            } catch (Exception e) {
                log.error("success={}", redisUtil.get(redisKey+REDIS_KEY_SUCCESS), e);
            }

        }

        return taskDTO;
    }

    @Override
    public TaskDTO queryByBizId(String appName, String bizId) {
        TaskDO taskDO = taskMapper.selectByBizId(appName, bizId);
        return taskConvert.convert(taskDO);
    }

    @Override
    public TaskItemDTO queryItemById(Long id) {

        TaskItemDO taskItemDO = taskMapper.selectItemById(id);

        return taskItemConvert.convert(taskItemDO);
    }

    @Override
    public int runTask(TaskDTO taskDTO) {

        String redisKey = getTaskRedisKey(taskDTO.getId());

        redisUtil.set(redisKey+REDIS_KEY_SUCCESS, "0");
        redisUtil.set(redisKey+REDIS_KEY_FAIL, "0");
        redisUtil.set(redisKey+REDIS_KEY_RUNNING, "0");
        redisUtil.expire(redisKey+REDIS_KEY_SUCCESS, timeout);
        redisUtil.expire(redisKey+REDIS_KEY_FAIL, timeout);
        redisUtil.expire(redisKey+REDIS_KEY_RUNNING, timeout);

        taskDTO.setStatus(TaskStatus.RUNNING);

        TaskDO taskDO = new TaskDO();
        taskDO.setId(taskDTO.getId());
        taskDO.setStatus(TaskStatus.RUNNING.code);
        taskDO.setVersion(taskDTO.getVersion());
        taskDO.setGmtStart(new Date());

        int sum = taskMapper.update(taskDO);
        return sum;
    }

    @Override
    public int reRunTask(TaskDTO taskDTO) {
        String redisKey = getTaskRedisKey(taskDTO.getId());

        redisUtil.set(redisKey+REDIS_KEY_SUCCESS, taskDTO.getSuccess().toString());
        redisUtil.set(redisKey+REDIS_KEY_FAIL, "0");
        redisUtil.set(redisKey+REDIS_KEY_RUNNING, "0");
        redisUtil.expire(redisKey+REDIS_KEY_SUCCESS, timeout);
        redisUtil.expire(redisKey+REDIS_KEY_FAIL, timeout);
        redisUtil.expire(redisKey+REDIS_KEY_RUNNING, timeout);

        taskDTO.setStatus(TaskStatus.RUNNING);

        TaskDO taskDO = new TaskDO();
        taskDO.setId(taskDTO.getId());
        taskDO.setStatus(TaskStatus.RUNNING.code);
        taskDO.setVersion(taskDTO.getVersion());
        taskDO.setGmtStart(new Date());

        int sum = taskMapper.update(taskDO);
        return sum;
    }

    @Override
    public int failTask(TaskDTO taskDTO) {
        return 0;
    }

    @Override
    public int successTask(TaskDTO taskDTO) {
        taskDTO.setStatus(TaskStatus.SUCCESS);
        return taskMapper.update(taskConvert.reconvert(taskDTO));
    }

    @Override
    public int runTaskItem(Long taskId, TaskItemDTO item) {
        item.setExecTime(item.getExecTime()+1);
        item.setStatus(TaskStatus.RUNNING);

        String key = getTaskRedisKey(taskId);

        int sum = taskMapper.updateItem(taskItemConvert.reconvert(item));
        item.setVersion(item.getVersion()+1);
        if (sum == 1) {
            redisUtil.incr(key+REDIS_KEY_RUNNING, 1);
        }
        log.debug("running id={} running={} success={} fail={}",
                item.getId(),
                redisUtil.get(key+REDIS_KEY_RUNNING),
                redisUtil.get(key+REDIS_KEY_SUCCESS),
                redisUtil.get(key+REDIS_KEY_FAIL)
        );
        return sum;
    }

    @Override
    public int failTaskItem(Long taskId,TaskItemDTO item) {

        if (item.getExecTime()==0) {
            item.setExecTime(1);
        }
        item.setStatus(TaskStatus.FAIL);
        String redisKey = getTaskRedisKey(taskId);
        int sum = taskMapper.updateItem(taskItemConvert.reconvert(item));
        item.setVersion(item.getVersion()+1);
        if (sum == 1) {
            redisUtil.incr(redisKey+REDIS_KEY_RUNNING, -1);
            redisUtil.incr(redisKey+REDIS_KEY_FAIL, 1);
        }

        return sum;
    }

    @Override
    public int failWithRetry(Long taskId, TaskItemDTO item) {
        if (item.getExecTime()==0) {
            item.setExecTime(1);
        }
        item.setStatus(TaskStatus.FAIL_NEED_RETRY);
        int sum = taskMapper.updateItem(taskItemConvert.reconvert(item));
        return sum;
    }

    @Override
    public int successTaskItem(Long taskId, TaskItemDTO item) {

        if (item.getExecTime()==0) {
            item.setExecTime(1);
        }
        item.setStatus(TaskStatus.SUCCESS);
        String redisKey = getTaskRedisKey(taskId);
        int sum = taskMapper.updateItem(taskItemConvert.reconvert(item));
        item.setVersion(item.getVersion()+1);
        if (sum==1) {
            redisUtil.incr(redisKey+REDIS_KEY_RUNNING, -1);
            redisUtil.incr(redisKey+REDIS_KEY_SUCCESS, 1);
        }
        log.debug("success id={} running={} success={} fail={}",
                item.getId(),
                redisUtil.get(redisKey+REDIS_KEY_RUNNING),
                redisUtil.get(redisKey+REDIS_KEY_SUCCESS),
                redisUtil.get(redisKey+REDIS_KEY_FAIL)
        );
        return sum;
    }

    @Override
    public int removeTaskItem(Long taskId, List<TaskItemDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return 0;
        }
        List<Long> idList = items.stream().map(TaskItemDTO::getId).collect(Collectors.toList());
        return taskMapper.removeTaskItems(taskId, idList);
    }


    private String getTaskRedisKey(Long taskId) {
        return redisPrefix + taskId+".";
    }
}
