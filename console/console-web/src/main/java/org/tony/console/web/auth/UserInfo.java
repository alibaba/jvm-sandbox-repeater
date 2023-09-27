package org.tony.console.web.auth;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/2/4 16:37
 */
@Data
public class UserInfo {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户在redis中的token
     */
    private String token;
}
