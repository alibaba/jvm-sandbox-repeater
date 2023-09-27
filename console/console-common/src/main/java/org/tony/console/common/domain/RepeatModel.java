package org.tony.console.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MockInvocation;

@Data
public class RepeatModel implements Serializable {

    private String repeatId;

    private boolean finish;

    private Object response;

    private Object originResponse;

    private Object diff;

    private Long cost;

    private String traceId;

    private List<MockInvocation> mockInvocations;

    /**
     * 扩展信息
     */
    private Map<String,String> extension;
}
