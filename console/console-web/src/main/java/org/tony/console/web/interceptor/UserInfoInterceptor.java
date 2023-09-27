package org.tony.console.web.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.tony.console.common.exception.BizException;
import org.tony.console.web.auth.UserInfoCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author peng.hu1
 * @Date 2023/2/6 20:37
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    private static String USER = "user_domain";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String user = request.getHeader("user_domain");
        if (user!=null) {
            UserInfoCache.setUser(user);
        } else {
            throw BizException.build("not auth, please login");
        }

        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserInfoCache.clear();
    }
}
