package com.alibaba.jvm.sandbox.repater.plugin.http;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * {@link HttpPlugin} http入口流量类型插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class HttpPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        // 拦截javax.servlet.http.HttpServlet#service(HttpServletRequest req, HttpServletResponse resp)
        EnhanceModel.MethodPattern mp = EnhanceModel.MethodPattern.builder()
                .methodName("service")
                .parameterType(new String[]{"javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse"})
                .build();
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("javax.servlet.http.HttpServlet")
                .methodPatterns(new EnhanceModel.MethodPattern[]{mp})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        // return null cause we override getEventListener
        return null;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new HttpStandaloneListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HTTP;
    }

    @Override
    public String identity() {
        return "http";
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void onLoaded() throws PluginLifeCycleException {
        new EventWatchBuilder(watcher)
                .onClass("org.springframework.web.method.support.InvocableHandlerMethod")
                .onBehavior("doInvoke")
                .onWatch(new AdviceListener() {

                    protected void before(Advice advice) throws Throwable {
                        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {
                            RepeatContext repeatContext = RepeatCache.getRepeatContext(Tracer.getTraceId());
                            if (repeatContext!=null) {
                                repeatContext.setCanMockDate(true);
                            }

                        }
                    }
                    protected void afterReturning(Advice advice) throws Throwable {
                        if (RepeatCache.isRepeatFlow(Tracer.getTraceId())) {
                            RepeatContext repeatContext = RepeatCache.getRepeatContext(Tracer.getTraceId());
                            if (repeatContext!=null) {
                                repeatContext.setCanMockDate(false);
                            }
                        }
                    }

                });
    }
}
