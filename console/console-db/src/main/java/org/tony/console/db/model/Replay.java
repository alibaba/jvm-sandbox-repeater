package org.tony.console.db.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class Replay implements Serializable {

    private static final long serialVersionUID = 1668489253484L;


    /**
    * 主键
    * 主键
    * isNullAble:0
    */
    private Long id;

    /**
    * 创建时间
    * isNullAble:0
    */
    private Date gmtCreate;

    /**
    * 修改时间
    * isNullAble:0
    */
    private Date gmtModified;

    /**
    * 应用名
    * isNullAble:0
    */
    private String appName;

    /**
    * 环境信息
    * isNullAble:0
    */
    private String environment;

    /**
    * 机器IP
    * isNullAble:0
    */
    private String ip;

    /**
    * 回放ID
    * isNullAble:0
    */
    private String repeatId;

    /**
    * 回放状态
    * isNullAble:0
    */
    private Integer status;

    /**
    * 链路追踪ID
    * isNullAble:1
    */
    private String traceId;

    /**
    * 回放耗时
    * isNullAble:1
    */
    private Long cost;

    /**
    * diff结果
    * isNullAble:1
    */
    private String diffResult;

    /**
     * 子调用比对结果
     */
    private String subInvokeDiffResult;

    /**
    * 回放结果
    * isNullAble:1
    */
    private String response;

    /**
    * mock过程
    * isNullAble:1
    */
    private String mockInvocation;

    /**
    * 是否回放成功
    * isNullAble:1
    */
    private boolean success;

    /**
    * 外键
    * isNullAble:1
    */
    private Long recordId;

    /**
     * 回放类型
     * 0： 采集回放
     * 1： 用例回放
     */
    private int type;

    /**
     * 测试用例
     */
    private String caseId;

    /**
     * 扩展信息
     */
    private Map<String, String> extend;

    /**
     * 失败原因
     */
    private String failReason;
}
