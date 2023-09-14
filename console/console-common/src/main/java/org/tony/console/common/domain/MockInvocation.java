package org.tony.console.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author peng.hu1
 * @Date 2023/1/10 18:06
 */
@Data
public class MockInvocation implements Serializable {

    private int index;
    private String traceId;
    private String repeatId;
    private boolean success;
    private boolean skip;
    private long cost;
    private String originUri;
    private String currentUri;
    private Object[] originArgs;
    private Object[] currentArgs;
}

