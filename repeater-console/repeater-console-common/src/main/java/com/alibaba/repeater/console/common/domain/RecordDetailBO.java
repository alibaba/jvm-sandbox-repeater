package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link RecordDetailBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
@ToString
public class RecordDetailBO extends RecordBO {

    private String request;

    private String response;

    private String subInvocations;
}
