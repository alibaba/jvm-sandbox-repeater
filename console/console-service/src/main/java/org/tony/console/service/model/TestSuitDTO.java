package org.tony.console.service.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.tony.console.common.enums.Status;
import org.tony.console.service.model.enums.TestSuitType;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2022/12/15 18:11
 */
@Data
public class TestSuitDTO {

    private Long id;

    private String name;

    private String appName;

    private Long parentId;

    private Date gmtCreate;

    private Date gmtUpdate;

    private TestSuitType type;

    private JSONObject extend;

    private Status status;

    /**
     * 全网回归标签
     */
    private Boolean regression;
}
