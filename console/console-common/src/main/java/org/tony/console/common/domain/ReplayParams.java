package org.tony.console.common.domain;

import lombok.*;

/**
 * {@link ReplayParams}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplayParams extends BaseParams {

    private String ip;

    private String repeatId;

    private String port;

    private boolean mock;

    private boolean single;

    private String env;
}
