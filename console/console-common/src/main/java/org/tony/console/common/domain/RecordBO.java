package org.tony.console.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * {@link RecordBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class RecordBO extends BaseBO implements java.io.Serializable {

    private String id;

    private Date gmtCreate;

    private Date gmtRecord;

    private String appName;

    private String environment;

    private String host;

    private String traceId;

    private String entranceDesc;

    private RecordType recordType;

    private List<Tag> tags;

    /**
     * 是否添加过case
     */
    private boolean added;

    /**
     * 版本信息
     */
    private int version;

    @Override
    public String toString() {
        return super.toString();
    }
}
