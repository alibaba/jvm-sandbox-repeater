package org.tony.console.service;

import java.util.Map;

/**
 * @author peng.hu1
 * @Date 2023/3/2 20:55
 */
public interface FeishuService {

    /**
     * 发送飞书群消息
     * @param webhook
     * @param template
     * @param params
     */
    public void send(String webhook, String template, Map<String, Object> params);
}
