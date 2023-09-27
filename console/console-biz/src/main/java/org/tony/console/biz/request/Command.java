package org.tony.console.biz.request;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.tony.console.common.exception.BizException;
import org.tony.console.common.request.BizRequest;
import org.tony.console.common.utils.VerifyUtil;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/5/15 14:26
 */
@Data
public class Command implements BizRequest {

    /**
     * 命令类型
     * 0： 回放
     * 1： 刷新配置
     */
    private Integer type;

    /**
     * 命令请求参数
     */
    private Map<String, String> requestParams;

    /**
     * 机器ip
     */
    private String ip;

    /**
     * 机器端口
     */
    private String port;

    /**
     * 区域
     */
    private String region;

    @Override
    public void check() throws BizException {
        VerifyUtil.verifyNotNull(type, "type is null");
        VerifyUtil.verifyNotNull(requestParams, "requestParams is null");
        VerifyUtil.verifyNotNull(port, "port is null");
        VerifyUtil.verifyNotBlank(ip, "ip is null");
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append("type=").append(type).append(",");
        builder.append("ip=").append(ip).append(",");
        builder.append("port=").append(port).append(",");
        builder.append("requestParams=").append(requestParams).append(",");
        builder.append("region=").append(region).append(",");
        builder.append(")");
        return builder.toString();
    }
}
