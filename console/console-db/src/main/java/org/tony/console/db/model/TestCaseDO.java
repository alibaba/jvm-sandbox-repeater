package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/13 21:20
 */
@Data
public class TestCaseDO {
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
     * 用例名称
     */
    private String caseName;

    /**
     * 测试套件id
     */
    private Long suitId;

    /**
     * 对应的记录id
     */
    private Long recordId;

    /**
     * 是否删除
     */
    private boolean delete;

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
     * 扩展信息
     */
    private String extend;

    private String user;
}
