package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.*;
import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.RepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
public abstract class AbstractInvocationProcessor implements InvocationProcessor {

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        return event.argumentArray;
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Type.RETURN) {
            return ((ReturnEvent) event).object;
        }
        return null;
    }

    @Override
    public Throwable assembleThrowable(ThrowsEvent event) {
        return event.throwable;
    }

    @Override
    public void doMock(BeforeEvent event, Boolean entrance, InvokeType type) throws ProcessControlException {
        /*
         * 获取回放上下文
         */
        RepeatContext context = RepeatCache.getRepeatContext(Tracer.getTraceId());
        /*
         * mock执行条件
         */
        if (!skipMock(event, entrance, context) && context != null && context.getMeta().isMock()) {
            try {
                /*
                 * 构建mock请求
                 */
                final MockRequest request = MockRequest.builder()
                        .argumentArray(this.assembleRequest(event))
                        .event(event)
                        .identity(this.assembleIdentity(event))
                        .meta(context.getMeta())
                        .recordModel(context.getRecordModel())
                        .traceId(context.getTraceId())
                        .type(type)
                        .repeatId(context.getMeta().getRepeatId())
                        .index(SequenceGenerator.generate(context.getTraceId()))
                        .build();
                /*
                 * 执行mock动作
                 */
                final MockResponse mr = StrategyProvider.instance().provide(context.getMeta().getStrategyType()).execute(request);
                /*
                 * 处理策略推荐结果
                 */
                switch (mr.action) {
                    case SKIP_IMMEDIATELY:
                        break;
                    case THROWS_IMMEDIATELY:
                        ProcessControlException.throwThrowsImmediately(mr.throwable);
                        break;
                    case RETURN_IMMEDIATELY:
                        ProcessControlException.throwReturnImmediately(assembleMockResponse(event, mr.invocation));
                        break;
                    default:
                        ProcessControlException.throwThrowsImmediately(new RepeatException("invalid action"));
                        break;
                }
            } catch (ProcessControlException pce) {
                throw pce;
            } catch (Throwable throwable) {
                ProcessControlException.throwThrowsImmediately(new RepeatException("unexpected code snippet here.", throwable));
            }
        }
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        return invocation.getResponse();
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        return new Identity(getType().name(), event.javaClassName, event.javaMethodName + "~" + event.javaMethodDesc, getExtra());
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return true;
    }

    /* 考虑到 event.argumentArray 中可能存在null的情况，无法还原原始类型；采用event.javaMethodDesc来还原 */

    @Deprecated
    private String getMethodSpec(BeforeEvent event) {
        List<Class<?>> classes = Lists.newArrayList();
        if (event.argumentArray != null && event.argumentArray.length > 0) {
            for (Object object : event.argumentArray) {
                classes.add(object.getClass());
            }
        }
        return getMethodDesc(event.javaMethodName, classes.toArray(new Class[0]));
    }

    @Deprecated
    protected String getMethodDesc(String methodName, Class<?>[] parameterTypes) {
        StringBuilder builder = new StringBuilder(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            builder.append("~");
            for (Class<?> parameterType : parameterTypes) {
                String className = parameterType.getSimpleName();
                builder.append(className.subSequence(0, 1));
            }
        }
        return builder.toString();
    }

    /**
     * 当前处理器类型
     *
     * @return 调用类型
     */
    abstract protected InvokeType getType();


    /**
     * identity额外透传字段
     *
     * @return extra map
     */
    protected Map<String, String>  getExtra() {
        return null;
    }

    /**
     * 是否需要跳过这次Mock;插件可以自己扩展;默认跳过入口的Mock
     *
     * @param event   before事件
     * @param context 回放上下文
     * @return 是否跳过
     */
    protected boolean skipMock(BeforeEvent event, Boolean entrance, RepeatContext context) {
        return entrance;
    }

    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        return false;
    }
}
