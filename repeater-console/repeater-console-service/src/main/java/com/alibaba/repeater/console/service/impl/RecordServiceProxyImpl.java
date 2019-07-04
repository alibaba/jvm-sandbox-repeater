package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.service.RecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * {@link RecordServiceProxyImpl} 示例存储服务代理实现
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("recordService")
public class RecordServiceProxyImpl implements RecordService {

    @Value("${console.use.localCache}")
    private boolean useLocalCache;

    @Resource(name = "recordServiceLocal")
    private RecordService recordServiceLocal;

    @Resource(name = "recordServiceMysql")
    private RecordService recordServiceMysql;

    @Override
    public RepeaterResult<String> saveRecord(String body) {
        return select().saveRecord(body);
    }

    @Override
    public RepeaterResult<String> saveRepeat(String body) {
        return select().saveRepeat(body);
    }

    @Override
    public RepeaterResult<String> get(String appName, String traceId) {
        return select().get(appName, traceId);
    }

    @Override
    public RepeaterResult<String> repeat(String appName, String traceId, String repeatId) {
        return select().repeat(appName, traceId, repeatId);
    }

    @Override
    public RepeaterResult<RepeatModel> callback(String repeatId) {
        return select().callback(repeatId);
    }

    private RecordService select() {
        return useLocalCache ? recordServiceLocal : recordServiceMysql;
    }
}
