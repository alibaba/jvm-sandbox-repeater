package com.alibaba.jvm.sandbox.repater.plugin.http;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperRequest;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperResponseCopier;
import com.alibaba.jvm.sandbox.repater.plugin.http.wrapper.WrapperTransModel;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.StandaloneSwitch;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.*;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Long.parseLong;


/**
 * {@link HttpStandaloneListener} 继承 {@link DefaultEventListener}但是由于http有同步异步两种策略，因此需要重写一些方法
 * <p>
 *
 * @author zhaoyb1990
 */
public class HttpStandaloneListener extends DefaultEventListener implements InvokeAdvice {

    private final ThreadLocal<WrapperTransModel> wtmRef = new ThreadLocal<WrapperTransModel>();

    HttpStandaloneListener(InvokeType invokeType,
                           boolean entrance,
                           InvocationListener listener,
                           InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    /**
     * 重写initContext；对于http请求；before事件里面
     *
     * @param event 事件
     */

    @Override
    protected void initContext(Event event) {
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent be = (BeforeEvent) event;
            Object request = be.argumentArray[0];
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = ((HttpServletRequest) request);
                // header透传开始回放；
                String traceIdX = req.getHeader(Constants.HEADER_TRACE_ID_X);
                if (StringUtils.isEmpty(traceIdX)){
                    traceIdX = req.getParameter(Constants.HEADER_TRACE_ID_X);
                }

                boolean single = false;
                if (req.getHeader(Constants.HEADER_SINGLE)!=null) {
                    single = true;
                }

                if (TraceGenerator.isValid(traceIdX)) {
                    RepeatMeta meta = new RepeatMeta();
                    meta.setAppName(ApplicationModel.instance().getAppName());
                    meta.setMock(true);
                    meta.setTraceId(traceIdX);
                    meta.setMatchPercentage(100);
                    meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
                    meta.setRepeatId(traceIdX);
                    RepeaterResult<RecordModel> pr = StandaloneSwitch.instance().getBroadcaster().pullRecord(meta);
                    if (pr.isSuccess()) {
                        Tracer.start();
                        RepeatContext context = new RepeatContext(meta, pr.getData(), Tracer.getTraceId(), single);
                        RepeatCache.putRepeatContext(context);
                        return;
                    }
                }
                // header透传traceId
                String traceId = req.getHeader(Constants.HEADER_TRACE_ID);
                if (StringUtils.isEmpty(traceId)){
                    traceId = req.getParameter(Constants.HEADER_TRACE_ID);
                }
                if (TraceGenerator.isValid(traceId)) {
                    Tracer.startAfterClean(traceId);
                    return;
                }
            }
        }
        super.initContext(event);
    }

    @Override
    protected boolean sample(Event event) {
        if (entrance && event.type == Event.Type.BEFORE) {
            boolean isSampleMain = Tracer.getContext().inTimeSample(invokeType);
            if (isSampleMain) {
                BeforeEvent beforeEvent = (BeforeEvent) event;

                Object request = beforeEvent.argumentArray[0];
                Object response = beforeEvent.argumentArray[1];
                if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
                    return false;
                }
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse resp = (HttpServletResponse) response;

                // 根据 requestURI 进行采样匹配
                if (!matchRequestURI(req.getRequestURI())) {
                    LogUtil.debug("current uri {} can't match any httpEntrancePatterns, ignore this request", req.getRequestURI());
                    return false;
                }
            }

            return isSampleMain;
        } else {
            final TraceContext context = Tracer.getContext();
            return context != null && context.isSampled();
        }
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        // 回放流量；入口直接返回
        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            return;
        }
        Object request = event.argumentArray[0];
        Object response = event.argumentArray[1];
        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //写死，提升效率
        if ("/status".equals(req.getRequestURI())) {
            Tracer.getContext().setSampled(false);
            return;
        }

        // 根据 requestURI 进行采样匹配
        if (!matchRequestURI(req.getRequestURI())) {
            LogUtil.info("current uri {} can't match any httpEntrancePatterns, ignore this request", req.getRequestURI());
            Tracer.getContext().setSampled(false);
            return;
        }

        WrapperResponseCopier wrapperRes = new WrapperResponseCopier(resp);
        WrapperRequest wrapperReq;
        try {
            wrapperReq = new WrapperRequest(req, wrapperRes, this);
        } catch (IOException e) {
            LogUtil.error("error occurred when assemble wrapper request", e);
            Tracer.getContext().setSampled(false);
            return;
        } catch (ServletException e) {
            LogUtil.error("error occurred when assemble wrapper request", e);
            Tracer.getContext().setSampled(false);
            return;
        }
        WrapperTransModel wtm = WrapperTransModel.build(wrapperReq);
        wtm.setBody(wrapperReq.getBody());
        wtm.copier = wrapperRes;
        wtm.request = wrapperReq;
        onRequest(wrapperReq, event);
        event.argumentArray[0] = wrapperReq;
        event.argumentArray[1] = wrapperRes;
        wtmRef.set(wtm);
    }

    private boolean isInSampleRate(String uri) {
        Map<String, Long> map = ApplicationModel.instance().getConfig().getHttpEntrancePatternsWithSampleRate();
        if (!map.containsKey(uri)) {
            return true;
        }
        if (parseLong(TraceGenerator.getSampleBit(Tracer.getContext().getTraceId())) % 10000 < map.get(uri)) {
            return true;
        }

        return false;
    }

    @Override
    protected void doThrow(ThrowsEvent event) {
        doFinish();
    }

    @Override
    protected void doReturn(ReturnEvent event) {
        doFinish();
    }

    private void doFinish() {
        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            RepeatCache.removeRepeatContext(Tracer.getTraceId());
            return;
        }
        Tracer.end();
        WrapperTransModel wtm = wtmRef.get();
        if (wtm == null) {
            LogUtil.warn("invalid request, no matched wtm found, traceId={}", Tracer.getTraceId());
            return;
        }
        onResponse(wtm.request, wtm);
        wtmRef.remove();
    }

    @Override
    public void onStartAsync(WrapperRequest request) {
        HttpInvocation invocation = (HttpInvocation) RecordCache.getInvocation(request.hashCode());
        if (invocation == null) {
            return;
        }
        invocation.setAsync(true);
    }

    @Override
    public void onComplete(WrapperRequest request, WrapperTransModel wtm) {
        HttpInvocation invocation = (HttpInvocation) RecordCache.getInvocation(request.hashCode());
        if (invocation == null) {
            return;
        }
        onFinish(invocation, wtm);
    }

    @Override
    public void onRequest(WrapperRequest request, BeforeEvent event) {
        HttpInvocation invocation = new HttpInvocation();
        invocation.setInit(true);
        invocation.setStart(System.currentTimeMillis());
        invocation.setIndex(1);
        invocation.setType(InvokeType.HTTP);
        invocation.setEntrance(true);
        invocation.setTraceId(Tracer.getTraceId());
        invocation.setInvokeId(event.invokeId);
        invocation.setProcessId(event.processId);
        invocation.setSerializeToken(ClassloaderBridge.instance().encode(event.javaClassLoader));
        RecordCache.cacheInvocation(request.hashCode(), invocation);
    }

    @Override
    public void onResponse(WrapperRequest request, WrapperTransModel wtm) {
        HttpInvocation invocation = (HttpInvocation) RecordCache.getInvocation(request.hashCode());
        if (invocation == null || invocation.isAsync()) {
            return;
        }
        try {
            wtm.setResponse(new String(wtm.copier.getResponseData(), wtm.copier.getCharacterEncoding()));
        } catch (Exception e) {
            LogUtil.error("error occurred when get response,message = {}", e.getMessage());
        }
        onFinish(invocation, wtm);
    }

    private void onFinish(HttpInvocation invocation, WrapperTransModel wtm) {
        if (invocation.isInit()) {
            assembleHttpAttribute(invocation, wtm);
            invocation.setEnd(System.currentTimeMillis());
            listener.onInvocation(invocation);
        }
    }

    private void assembleHttpAttribute(HttpInvocation invocation, WrapperTransModel wtm) {
        Identity identity = new Identity(InvokeType.HTTP.name(), wtm.getRequestURI(), "", null);
        invocation.setRequestURL(wtm.getRequestURL());
        invocation.setRequestURI(wtm.getRequestURI());
        invocation.setPort(wtm.getPort());
        invocation.setMethod(wtm.getMethod());
        invocation.setContentType(wtm.getContentType());
        invocation.setHeaders(wtm.getHeaders());
        invocation.setBody(wtm.getBody());
        invocation.setParamsMap(wtm.getParamsMap());
        // 翻译WrapperTransferModel参数
        Map<String, Object> params = new HashMap<String, Object>(8);
        params.put("requestURI", wtm.getRequestURI());
        params.put("requestURL", wtm.getRequestURL());
        params.put("method", wtm.getMethod());
        params.put("port", wtm.getPort());
        params.put("contentType", wtm.getContentType());
        params.put("headers", wtm.getHeaders());
        params.put("paramsMap", wtm.getParamsMap());
        params.put("body", wtm.getBody());
//        Map<String, Object> multipart = wtm.getRequest().getMultipart();
        params.put("isMultipart", wtm.getRequest().isMultipart());

        if (wtm.getRequest().getFileName()!=null) {
            params.put("filename", wtm.getRequest().getFileName());
        }

        invocation.setMultipart(wtm.getRequest().isMultipart());
        invocation.setFilename(wtm.getRequest().getFileName());
        invocation.setFileContent(wtm.getRequest().getFileContent());
        invocation.setPartName(wtm.getRequest().getPartName());

        invocation.setRequest(new Object[]{params});
        invocation.setResponse(wtm.getResponse());
        invocation.setIdentity(identity);
    }

    /**
     * 是否命中需要采样的requestURI
     *
     * @param requestURI 请求URI
     * @return 是否命中
     */
    private boolean matchRequestURI(String requestURI) {
        Set<String> patterns = ApplicationModel.instance().getConfig().getHttpEntrancePatternsWithSampleRate().keySet();
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (requestURI.matches(pattern)) {
                //新增一个采样率计算逻辑
                if (isInSampleRate(pattern)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
