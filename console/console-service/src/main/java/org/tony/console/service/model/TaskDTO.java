package org.tony.console.service.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.tony.console.common.enums.TaskStatus;
import org.tony.console.common.enums.Env;
import org.tony.console.service.model.enums.TaskType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/1/4 13:08
 */
@Data
public class TaskDTO implements Serializable {

    private static final long serialVersionUID = 1668489253484L;

    private Long id;

    private String name;

    private String appName;

    private Date gmtCreate;

    private Date gmtUpdate;

    private Date gmtStart;

    private TaskStatus status;

    private TaskType type;

    /**
     * 业务幂等号
     */
    private String bizId;

    /**
     * 总个数
     */
    private Integer total;

    /**
     * 成功个数
     */
    private Integer success;

    /**
     * 失败个数
     */
    private Integer fail;

    private Integer running;

    /**
     * 扩展信息
     */
    private JSONObject extend;

    private int version;

    private Integer retryTime;

    /**
     * 延后执行
     */
    private Boolean lazy;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 环境
     */
    Env env;

    /**
     * 部署信息
     */
    private String deployTaskId;

    /**
     * 部署实例
     */
    private String deployInstName;

    public TaskDTO() {
        this.total = 0;
        this.success = 0;
        this.fail = 0;
        this.running = 0;
    }


    public void addExtend(String key, Object o) {
        if (this.extend == null) {
            this.extend = new JSONObject();
        }

        this.extend.put(key, o);
    }

    public Integer getRetryTime() {
        if (this.retryTime == null) {
            return 0;
        }

        return retryTime;
    }
}
