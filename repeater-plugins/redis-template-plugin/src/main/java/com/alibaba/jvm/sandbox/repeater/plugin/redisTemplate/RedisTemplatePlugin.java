package com.alibaba.jvm.sandbox.repeater.plugin.redisTemplate;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/16 09:37
 */
@MetaInfServices(InvokePlugin.class)
public class RedisTemplatePlugin extends AbstractInvokePluginAdapter {
    @Override
    public InvokeType getType() {
        return InvokeType.SPRING_REDIS_TEMPLATE;
    }

    @Override
    public String identity() {
        return InvokeType.SPRING_REDIS_TEMPLATE.name();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel defaultValueOperations = EnhanceModel.builder()
                .classPattern("org.springframework.data.redis.core.DefaultValueOperations")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "get",
                        "getAndSet",
                        "increment",
                        "decrement",
                        "append",
                        "multiGet",
                        "multiSet",
                        "multiSetIfAbsent",
                        "set",
                        "setIfAbsent",
                        "setIfPresent",
                        "size",
                        "setBit",
                        "getBit",
                        "bitField"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        EnhanceModel defaultHashOperations = EnhanceModel.builder()
                .classPattern("org.springframework.data.redis.core.DefaultHashOperations")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "get",
                        "hasKey",
                        "increment",
                        "keys",
                        "size",
                        "lengthOfValue",
                        "putAll",
                        "multiGet",
                        "put",
                        "putIfAbsent",
                        "values",
                        "delete",
                        "entries"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        EnhanceModel defaultListOperations = EnhanceModel.builder()
                .classPattern("org.springframework.data.redis.core.DefaultListOperations")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "index",
                        "leftPop",
                        "leftPush",
                        "leftPushAll",
                        "leftPushIfPresent",
                        "leftPush",
                        "size",
                        "range",
                        "remove",
                        "rightPop",
                        "rightPush",
                        "rightPushAll",
                        "rightPushIfPresent",
                        "rightPopAndLeftPush",
                        "rightPopAndLeftPush",
                        "set",
                        "trim"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        EnhanceModel defaultSetOperations = EnhanceModel.builder()
                .classPattern("org.springframework.data.redis.core.DefaultSetOperations")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "add",
                        "difference",
                        "differenceAndStore",
                        "intersect",
                        "intersectAndStore",
                        "isMember",
                        "members",
                        "move",
                        "randomMember",
                        "distinctRandomMembers",
                        "randomMembers",
                        "remove",
                        "pop",
                        "size",
                        "union",
                        "unionAndStore",
                        "scan"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        EnhanceModel defaultReactiveZSetOperations = EnhanceModel.builder()
                .classPattern("org.springframework.data.redis.core.DefaultReactiveZSetOperations")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "add",
                        "addAll",
                        "remove",
                        "incrementScore",
                        "rank",
                        "reverseRank",
                        "range",
                        "rangeWithScores",
                        "rangeByScore",
                        "rangeByScoreWithScores",
                        "rangeByScore",
                        "rangeByScoreWithScores",
                        "reverseRange",
                        "reverseRangeWithScores",
                        "reverseRangeByScore",
                        "reverseRangeByScoreWithScores",
                        "reverseRangeByScore",
                        "reverseRangeByScoreWithScores",
                        "scan",
                        "count",
                        "size",
                        "score",
                        "removeRange",
                        "removeRangeByScore",
                        "unionAndStore",
                        "intersectAndStore",
                        "rangeByLex",
                        "delete"
                ))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Arrays.asList(
                defaultValueOperations,
                defaultHashOperations,
                defaultSetOperations,
                defaultReactiveZSetOperations
        );
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RedisTemplateProcessor(getType());
    }
}
