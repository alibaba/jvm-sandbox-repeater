package org.tony.console.mongo.model;

import lombok.Data;
import org.tony.console.common.domain.Tag;

import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/1/30 14:35
 */
@Data
public class RecordMDO {

    private String id;

    private String traceId;

    private Date gmtRecord;

    private String entranceDesc;

    private String appName;

    private String env;

    private String host;

    private List<Tag> tags;

    private Boolean addCase;

    private String recordType;

    private int version;
}
