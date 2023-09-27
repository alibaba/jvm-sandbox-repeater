package org.tony.console.biz.components;

import java.util.HashMap;

/**
 * @author peng.hu1
 * @Date 2022/12/16 16:12
 */
public class BizSession<K,V> extends HashMap {

    public <T> void addData(String key, T data) {
        this.put(key, data);
    }

    public <T> T getData(String key) {
        return (T) this.get(key);
    }
}
