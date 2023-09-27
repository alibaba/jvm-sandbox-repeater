package org.tony.console.service.model.config;

import lombok.Data;
import org.tony.console.common.enums.Env;


/**
 * 每日回归配置
 * @author peng.hu1
 * @Date 2023/3/27 11:16
 */
@Data
public class AppDailyTestConfigDTO {

    /**
     * 每日回归是否开启
     */
    private Boolean open;

    /**
     * 环境
     */
    private Env env;

    /**
     * 时分秒
     */
    private String time;

    public AppDailyTestConfigDTO() {
        open = false;
        env = Env.TEST;
        time = "23:00:00";
    }
}
