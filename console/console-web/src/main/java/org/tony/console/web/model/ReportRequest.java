package org.tony.console.web.model;

import lombok.Data;
import org.tony.console.common.domain.ModuleStatus;

/**
 * @author peng.hu1
 * @Date 2023/5/15 10:02
 */
@Data
public class ReportRequest {

    private String appName;

    private String environment;

    private String ip;

    private String port;

    private String version;

    private ModuleStatus status;
}
