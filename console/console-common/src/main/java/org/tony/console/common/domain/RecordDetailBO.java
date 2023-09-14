package org.tony.console.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

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

    private Object[] requestObj;

    private String response;

    private Object responseObj;

    private String responseType;

    private List<InvocationBO> subInvocations;

}
