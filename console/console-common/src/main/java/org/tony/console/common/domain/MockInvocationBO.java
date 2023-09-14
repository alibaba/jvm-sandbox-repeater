package org.tony.console.common.domain;

import com.alibaba.jvm.sandbox.repeater.plugin.diff.DifferenceDO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link MockInvocationBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class MockInvocationBO extends BaseBO {
    private Integer index;
    private Boolean success;
    private Boolean skip;
    private Long cost;
    private String originUri;
    private String currentUri;
    private String originArgs;
    private String currentArgs;
    private List<DifferenceDO> diffs;
    private Boolean compareSuccess;

    @Override
    public String toString() {
        return super.toString();
    }
}
