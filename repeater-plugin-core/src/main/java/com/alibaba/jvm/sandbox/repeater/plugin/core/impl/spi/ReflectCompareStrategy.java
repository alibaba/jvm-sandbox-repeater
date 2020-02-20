package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * {@link ReflectCompareStrategy}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(MockStrategy.class)
public class ReflectCompareStrategy extends AbstractMockStrategy {

    @Override
    protected SelectResult select(MockRequest request) {
        final List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (CollectionUtils.isEmpty(subInvocations)) {
            return null;
        }
        Object[] current = request.getArgumentArray();
        java.util.List<Invocation> target = Lists.newArrayList();
        // step1:URI匹配,目前做精确匹配，后续可能需要考虑替换
        for (Invocation invocation : subInvocations) {
            if (StringUtils.equals(invocation.getIdentity().getUri(), request.getIdentity().getUri())) {
                target.add(invocation);
            }
        }
        if (CollectionUtils.isEmpty(target)) {
            log.error("can't find any sub invocation, strategy={}, identity={}", type().name(), request.getIdentity().getUri());
            return SelectResult.builder().match(false).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
        }
        // 根据index排序
        Collections.sort(target, new Comparator<Invocation>() {
            @Override
            public int compare(Invocation o1, Invocation o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        Map<Integer, Invocation> invocationMap = Maps.newHashMap();
        // step2: 反射对比；默认忽略时间戳
        for (Invocation invocation : target) {
            Object[] origin = invocation.getRequest();
            com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable comparable = ComparableFactory.instance()
                    .create(com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator.CompareMode.LENIENT_DATES);
            CompareResult result = comparable.compare(origin, current);
            if (!result.hasDifference()) {
                log.info("find target invocation by {},index={},identity={}", type().name(), request.getIndex(), request.getIdentity().getUri());
                Iterator<Invocation> ite = subInvocations.iterator();
                while (ite.hasNext()) {
                    if (invocation.equals(ite.next())) {
                        ite.remove();
                        break;
                    }
                }
                return SelectResult.builder().match(true).invocation(invocation).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
            }
            invocationMap.put(result.getDifferences().size(), invocation);
        }
        // 如果没有找到，返回差异最少的一条
        List<Integer> scores = new ArrayList<Integer>(invocationMap.keySet());
        Collections.sort(scores, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                return o2 - o1 > 0 ? 1 : -1;
            }
        });
        Integer diffCount = scores.get(0);
        Invocation invocation = invocationMap.get(diffCount);
        log.info("find invocation by {}, but have many difference,different count={},identity={}, originRequest={},currentRequest={}", type().name(),
                diffCount, request.getIdentity().getUri(), invocation.getRequest(), request.getArgumentArray());
        return SelectResult.builder().match(false).invocation(invocation).cost(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).build();
    }

    @Override
    public StrategyType type() {
        return StrategyType.DEFAULT;
    }
}
