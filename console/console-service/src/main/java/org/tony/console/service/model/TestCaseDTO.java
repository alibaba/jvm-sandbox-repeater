package org.tony.console.service.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.tony.console.common.domain.RecordType;
import org.tony.console.db.model.Record;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2022/12/14 13:50
 */
@Data
public class TestCaseDTO {

    /**
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
    private JSONObject extend;

    /**
     * record
     */
    private Record record;

    private RecordType recordType;

    private String user;

    /**
     * 排序配置
     */
    private HashMap<String, String> sortConfig;

    public RecordType getRecordType() {
        if (recordType!=null){
            return recordType;
        }

        if (StringUtils.isNotEmpty(entranceDesc)) {
            if (entranceDesc.startsWith("java")) {
                return RecordType.JAVA;
            } else {
                return RecordType.HTTP;
            }
        }

        return RecordType.HTTP;
    }

    public JSONObject getExtend() {
        if (extend == null) {
            extend = new JSONObject();
        }

        return extend;
    }
}
