package com.alibaba.jvm.sandbox.repeater.plugin.java;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * {@link }
 * <p>
 *
 * @author zhaoyb1990
 */
final class JavaPluginUtils {

    /**
     * 判断两次的behavior推送是否有变化
     *
     * @param source 行为集合
     * @param target 行为集合
     * @return 是否发生变化 true 是  false 否
     */
    static boolean hasDifference(List<Behavior> source, List<Behavior> target) {
        if (CollectionUtils.isEmpty(source) && CollectionUtils.isNotEmpty(target)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(source) && CollectionUtils.isEmpty(target)) {
            return true;
        }
        if (source.size() != target.size()) {
            return true;
        }
        return calcHash(source) != calcHash(target);
    }

    private static int calcHash(List<Behavior> behaviors) {
        int hashCode = 0;
        for (Behavior behavior : behaviors) {
            hashCode += behavior.getClassPattern().hashCode();
            hashCode += Arrays.hashCode(behavior.getMethodPatterns());
        }
        return hashCode;
    }
}
