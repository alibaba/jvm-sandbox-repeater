package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * {@link RecordBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class RecordBO extends BaseBO implements java.io.Serializable {

    private Long id;

    private Date gmtCreate;

    private Date gmtRecord;

    private String appName;

    private String environment;

    private String host;

    private String traceId;

    private String entranceDesc;

    @Override
    public String toString() {
        return super.toString();
    }
}
