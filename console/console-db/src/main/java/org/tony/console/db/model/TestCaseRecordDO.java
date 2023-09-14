package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/14 18:47
 */
@Data
public class TestCaseRecordDO {
    /**
     * 主键
     * 主键
     * isNullAble:0
     */
    private Long id;

    /**
     * 用例id
     */
    private String caseId;

    /**
     * 创建时间
     * isNullAble:0
     */
    private Date gmtCreate;

    /**
     * 录制时间
     * isNullAble:0
     */
    private Date gmtRecord;

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
    private String host;

    /**
     * 链路追踪ID
     * isNullAble:0
     */
    private String traceId;

    /**
     * 链路追踪ID
     * isNullAble:0
     */
    private String entranceDesc;

    /**
     * 记录序列化信息
     * isNullAble:0
     */
    private String wrapperRecord;

    /**
     * 请求参数JSON
     * isNullAble:0
     */
    private String request;

    /**
     * 返回值JSON
     * isNullAble:0
     */
    private String response;

    /**
     * 流量类型
     */
    private String type;
}
