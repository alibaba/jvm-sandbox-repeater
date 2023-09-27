package org.tony.console.web.auth;

/**
 * @author peng.hu1
 * @Date 2023/2/6 20:42
 */
public class UserInfoCache {

    private static ThreadLocal<String> userCache = new ThreadLocal<>();

    public static String getUser() {
       return userCache.get();
    }

    public static void setUser(String user) {
        userCache.set(user);
    }

    public static void clear() {
        userCache.set(null);
    }
}
