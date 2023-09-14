package org.tony.console.biz.components.createTask;

import org.tony.console.biz.components.BizComService;
import org.tony.console.biz.request.CreateTestTaskBizRequest;


/**
 * 创建任务组件服务
 */
public interface CreateTaskComponent  extends BizComService<CreateTestTaskBizRequest> {

    public final static String KEY_MODULE_LIST = "module_list";

    public final static String KEY_TASK_ID = "task_id";

    public final static String KEY_DEPLOY_TASK = "deploy_task";
}
