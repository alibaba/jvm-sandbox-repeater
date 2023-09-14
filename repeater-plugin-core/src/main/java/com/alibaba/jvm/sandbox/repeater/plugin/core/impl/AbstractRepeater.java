package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AbstractRepeater} 抽象的回放实现；统一回放基本流程，包括hook和消息反馈，实现类是需要关心{@code executeRepeat}执行回放
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractRepeater implements Repeater {

    protected static Logger log = LoggerFactory.getLogger(AbstractRepeater.class);

    private Broadcaster broadcaster;

    @Override
    public void repeat(RepeatContext context) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        RepeatModel record = new RepeatModel();
        record.setRepeatId(context.getMeta().getRepeatId());
        record.setTraceId(context.getTraceId());
        copyExtension(record, context.getMeta());
        try {
            // 根据之前生成的traceId开启追踪
            Tracer.start(context.getTraceId());
            // before invoke advice
            RepeatInterceptorFacade.instance().beforeInvoke(context.getRecordModel());
            Object response = executeRepeat(context);
            // after invoke advice
            RepeatInterceptorFacade.instance().beforeReturn(context.getRecordModel(), response);
            stopwatch.stop();
            record.setCost(stopwatch.elapsed(TimeUnit.MILLISECONDS));
            record.setFinish(true);
            record.setResponse(response);
            record.setMockInvocations(RepeatCache.getMockInvocation(context.getTraceId()));
        } catch (Exception e) {
            stopwatch.stop();
            record.setCost(stopwatch.elapsed(TimeUnit.MILLISECONDS));

            if (context.getThrowableSerialized()==null) {
                record.setResponse(e);
            } else {
                //说明是预期内的异常，这个时候mock过程也需要体现
                record.setResponse(null);
                record.setMockInvocations(RepeatCache.getMockInvocation(context.getTraceId()));
                record.setThrowableSerialized(context.getThrowableSerialized());
            }
            record.setFinish(true);

        } finally {
            //这里主动清理一次缓存
            //RepeatCache.removeInvocation(context.getTraceId());
            Tracer.end();
        }
        sendRepeat(record);
        RepeatCache.removeInvocation(context.getTraceId());
        RepeatCache.removeRepeatContext(context.getTraceId());
    }

    private void copyExtension(RepeatModel repeatModel, RepeatMeta meta) {
        for (Map.Entry<String, String> entry : meta.getExtension().entrySet()) {
            repeatModel.getExtension().put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return config != null && config.getRepeatIdentities().contains(identity());
    }

    @Override
    public void setBroadcast(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    private void sendRepeat(RepeatModel record) throws RuntimeException {
        if (broadcaster == null) {
            log.error("no valid broadcaster found, ensure that Repeater#setBroadcast has been called before Repeater#repeat");
            return;
        }
        broadcaster.sendRepeat(record);
    }

    /**
     * 执行回放动作
     *
     * @param context 回放上下文
     * @return 返回结果
     * @throws Exception 异常信息
     */
    protected abstract Object executeRepeat(RepeatContext context) throws Exception;
}
