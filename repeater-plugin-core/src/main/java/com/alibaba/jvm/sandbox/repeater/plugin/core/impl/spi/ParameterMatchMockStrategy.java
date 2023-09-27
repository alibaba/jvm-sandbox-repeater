package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.InvocationHandlerFacade;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.ApplicationModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DynamicConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

/**
 * <p>
 * 参数相似度匹配策略
 *
 * @author zhaoyb1990
 */
@MetaInfServices(MockStrategy.class)
public class ParameterMatchMockStrategy extends AbstractMockStrategy {

    @Override
    public SelectResult select(MockRequest request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        List<Invocation> target = Lists.newArrayList();
        // 先根据URI进行过滤
        if (CollectionUtils.isNotEmpty(subInvocations)) {
            synchronized (subInvocations) {
                for (int i=0; i<subInvocations.size(); i++) {
                    Invocation invocation = subInvocations.get(i);
                    if (invocation.getIdentity().getUri().equals(request.getIdentity().getUri())) {
                        target.add(invocation);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(target)) {
            log.error("can't find any sub invocation type={},identity={}", type().name() , request.getIdentity().getUri());
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }
        Invocation i = target.get(0);
        Serializer serializer = SerializerWrapper.getSerializer(i.getSerializeType());

        String requestSerialized;
        try {
            requestSerialized = serializer.serialize2String(request.getArgumentArray(), request.getEvent().javaClassLoader);
        } catch (Exception e) {
            log.error("serialize request occurred error, identity={}", type().name(), e);
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }
        // 根据index排序
        Collections.sort(target, new Comparator<Invocation>() {
            @Override
            public int compare(Invocation o1, Invocation o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        Map<Double,Invocation> invocationMap = Maps.newHashMap();

        //这里表示按照顺序来取
        if (target.size()==1) {
            Invocation invocation = target.get(0);
            //从子调用列表中剔除
            synchronized (subInvocations){
                subInvocations.removeIf(item->item.equals(invocation));
            }
            return SelectResult.builder().match(true).invocation(invocation).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }

        // 计算相似度;根据相似度进行排序
        for (Invocation invocation : target) {
            double similarity;
            try {
                similarity = calcSimilarity(invocation, request, requestSerialized);
            } catch (SerializeException e) {
                log.error("serialize request occurred error, identity={}", type().name(), e);
                return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
            }
            // 如果匹配就直接返回了
            if (similarity >= request.getMeta().getMatchPercentage() / 100) {
                //从子调用列表中剔除
                synchronized (subInvocations){
                    subInvocations.removeIf(item->item.equals(invocation));
                }
                log.debug("find target invocation by {},identity={},invocation={}", type().name(), request.getIdentity().getUri(), invocation);
                return SelectResult.builder().match(true).invocation(invocation).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
            }
            invocationMap.put(similarity, invocation);
        }
        // 如果没有找到，返回相似度最高的一条
        List<Double> scores = new ArrayList<Double>(invocationMap.keySet());
        Collections.sort(scores, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                if (Objects.equal(o1,o2)) {
                    return 0;
                }
                return o2 - o1 > 0 ? 1 : -1;
            }
        });
        Double similarity = scores.get(0);
        Invocation invocation = invocationMap.get(similarity);
        log.debug("find invocation by {},but similarity not match similarity={},identity={}, invocation={}", type().name(), similarity, request.getIdentity().getUri(), invocation);
        //从子调用列表中剔除
        synchronized (subInvocations){
            subInvocations.removeIf(item->item.equals(invocation));
        }
        return SelectResult.builder().match(true).invocation(invocation).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
    }

    @Override
    public StrategyType type() {
        return StrategyType.PARAMETER_MATCH;
    }

    private double calcSimilarity(Invocation invocation , MockRequest request, String requestSerialized) throws SerializeException {
        String requestSerializedTarget;
        if (CollectionUtils.isNotEmpty(request.getModifiedInvocationIdentity()) &&
            request.getModifiedInvocationIdentity().contains(invocation.getIdentity())) {
            Serializer serializer = SerializerWrapper.getSerializer(invocation.getSerializeType());
            requestSerializedTarget = serializer.serialize2String(invocation.getRequest(),request.getEvent().javaClassLoader);
        } else {
            requestSerializedTarget = invocation.getRequestSerialized();
        }
        int distance = StringUtils.getLevenshteinDistance(requestSerialized, requestSerializedTarget);
        return 1 - (double) distance / Math.max(requestSerialized.length(), requestSerializedTarget.length());
    }

    @Override
    protected MockResponse executeWithOutInvocation(final MockRequest request) {
        DynamicConfig dynamicConfig = ApplicationModel.instance().getDynamicConfig();
        if (dynamicConfig == null) {
            return null;
        }
        Set<String> skipMockIdentities = dynamicConfig.getSkipMockIdentities();

        //如果配置了跳过
        if (skipMockIdentities.contains(request.getIdentity().getUri())) {
            return MockResponse.builder()
                    .action(MockResponse.Action.SKIP_IMMEDIATELY)
                    .build();
        }

        MockResponse response = InvocationHandlerFacade.instance().executeNotFundInvocation(request);
        return response;
    }
}
