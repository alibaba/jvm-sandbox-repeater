package org.tony.console.biz.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tony.console.biz.Constant;
import org.tony.console.biz.TestTaskBizService;
import org.tony.console.biz.request.UpdateTaskItemRequest;
import org.tony.console.db.model.Replay;

import javax.annotation.Resource;

/**
 * @author peng.hu1
 * @Date 2023/1/6 14:27
 */
@Slf4j
@Component
public class ReplayEventListener {

    @Resource
    TestTaskBizService testTaskBizService;

    @EventListener(Replay.class)
    public void getReplay(Replay replay) {
        if (replay.getExtend()==null) {
            return;
        }

        try {
            if (replay.getExtend().containsKey(Constant.TASK_ITEM_ID)) {
                String taskItemIdString = replay.getExtend().get(Constant.TASK_ITEM_ID);

                Long id = Long.valueOf(taskItemIdString);

                UpdateTaskItemRequest request = new UpdateTaskItemRequest();
                request.setTaskItemId(id);
                request.setRepeatId(replay.getRepeatId());
                request.setCost(replay.getCost());

                if (replay.isSuccess()) {
                    testTaskBizService.successTaskItem(request);
                } else {
                    testTaskBizService.failTaskItem(request);
                }
            }
        } catch (Exception e) {
            log.error("System error", e);
        }
    }
}
