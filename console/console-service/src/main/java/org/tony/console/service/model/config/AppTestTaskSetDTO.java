package org.tony.console.service.model.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 回归用例集
 * @author peng.hu1
 * @Date 2023/3/2 13:47
 */
@Data
public class AppTestTaskSetDTO {

    private Boolean open;

    /**
     * 回归的taskId集合
     */
    private Set<Long> taskIdSet;

    private List<String> feishuWebHooks;

    /**
     * 用例失败重试次数
     */
    private Integer failRetryTime;

    public AppTestTaskSetDTO() {
        open = false;
        taskIdSet = new HashSet<>();
        feishuWebHooks = new ArrayList<>();
        failRetryTime = 1;
    }
}
