package org.tony.console.biz.model;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/1/6 09:22
 */
@Data
public class TestCaseExecResultVO {
    /**
     * 结果的id
     */
    private Long id;

    private String appName;

    /**
     * case的id
     */
    private String caseId;

    /**
     * 入口
     */
    private String entrance;

    /**
     * 类型
     */
    private String invokeType;

    /**
     * case的名称
     */
    private String caseName;

    /**
     * 状态
     */
    private String status;

    /**
     * 耗时
     */
    private String duration;

    /**
     * 执行的ip
     */
    private String ip;

    /**
     * 回放id
     */
    private String repeatId;

    private Long taskId;
}
