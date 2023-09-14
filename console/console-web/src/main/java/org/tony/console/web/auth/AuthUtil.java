package org.tony.console.web.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tony.console.service.redis.RedisUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/2/4 16:26
 */
@Component
public class AuthUtil {

    @Resource
    RedisUtil redisUtil;

    @Value("${sso.url}")
    private String baseUrl;

    @Value("${app.client.id}")
    private String clientId;

    public String getAuthorizeUrl() {
        return baseUrl + "/oauth2/authorize";
    }

    public String getClientId() {
        return this.clientId;
    }

    public boolean isValid(String token) {
        String v = redisUtil.get(token);
        if (v==null) {
            return false;
        }

        return true;
    }

    public UserInfo loginWithCode(String code, String redirectUri) {

        //这里进行登录，写死就好
        UserInfo userInfo = new UserInfo();
        userInfo.setId("xiuzhu");

        return null;
    }


}
