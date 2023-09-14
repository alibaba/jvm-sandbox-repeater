package org.tony.console.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link ModuleConfigParams}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class ModuleConfigParams extends BaseParams {

    private String appName;

    private String environment;

    private String config;
}
