package com.alibaba.jvm.sandbox.repater.plugin.http;

import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.HttpInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import org.apache.commons.collections4.MapUtils;
import org.kohsuke.MetaInfServices;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link HttpRepeater} HTTP类型入口回放器;
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class HttpRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!(invocation instanceof HttpInvocation)) {
            throw new RepeatException("type miss match, required HttpInvocation but found " + invocation.getClass().getSimpleName());
        }
        HttpInvocation hi = (HttpInvocation) invocation;
        Map<String, String> extra = new HashMap<String, String>(2);
        // 透传当前生成的traceId到http线程 HttpStandaloneListener#initConetxt
        extra.put(Constants.HEADER_TRACE_ID, context.getTraceId());
        // 直接访问本机,默认全都走http，不关心protocol
        StringBuilder builder = new StringBuilder()
                .append("http")
                .append("://")
                .append("127.0.0.1")
                .append(":")
                .append(hi.getPort())
                .append(hi.getRequestURI());
        String url = builder.toString();
        Map<String, String> headers = rebuildHeaders(hi.getHeaders(), extra);
        HttpUtil.Resp resp = HttpUtil.invoke(url, hi.getMethod(), headers, hi.getParamsMap(), hi.getBody());
        return resp.isSuccess() ? resp.getBody() : resp.getMessage();
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HTTP;
    }

    @Override
    public String identity() {
        return "http";
    }

    /**
     * 重组后的headers
     *
     * @param headers 录制的header
     * @param extra   额外透传header
     * @return 重组后的headers
     */
    private Map<String, String> rebuildHeaders(Map<String, String> headers, Map<String, String> extra) {
        if (MapUtils.isEmpty(headers)) {
            return extra;
        }
        // 移除掉录制时候的traceId
        headers.remove(Constants.HEADER_TRACE_ID.toLowerCase());
        headers.putAll(extra);
        return headers;
    }
}
