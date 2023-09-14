package org.tony.console.db.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.tony.console.common.domain.RecordType;

import java.io.Serializable;
import java.util.Date;

@Data
public class Record implements Serializable {

    private static final long serialVersionUID = 1668489246262L;


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
     * 是否添加过0: 没有添加， 1：添加过
     */
    private int add;

    /**
     * 版本信息
     */
    private int version;

    /**
     * 类型
     */
    private String type;

    /**
     * 扩展信息
     */
    private String extend;


    public String getType() {
        if (StringUtils.isNotEmpty(type)) {
            return type;
        }

        if (entranceDesc.startsWith("java")) {
            this.type = RecordType.JAVA.type;
        } else {
            this.type = RecordType.HTTP.type;
        }

        return type;
    }
}
