package org.tony.console.service.model;

import lombok.Data;
import org.tony.console.common.domain.InvocationBO;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/29 13:42
 */
@Data
public class TestCaseDetailDTO extends TestCaseDTO{

    private String request;

    private Object[] requestObj;

    private String response;

    private List<InvocationBO> subInvocations;
}
