package com.alibaba.jvm.sandbox.repeater.plugin.core.bridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;

/**
 * {@link RepeaterBridge} 回放器桥接器
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeaterBridge {

    private RepeaterBridge() {}

    private volatile Map<InvokeType, Repeater> cached = new HashMap<InvokeType, Repeater>();

    public static RepeaterBridge instance() {
        return RepeaterBridge.LazyInstanceHolder.INSTANCE;
    }

    public void build(List<Repeater> rs) {
        if (rs == null || rs.isEmpty()) { return; }
        // reset repeat'er container
        cached.clear();
        for (Repeater repeater : rs) {
            cached.put(repeater.getType(), repeater);
        }
    }

    private final static class LazyInstanceHolder {
        private final static RepeaterBridge INSTANCE = new RepeaterBridge();
    }

    /**
     * 选择合适的回放器
     *
     * @param type 调用类型
     * @return 回放器
     */
    public Repeater select(InvokeType type) {
        return cached.get(type);
    }
}
