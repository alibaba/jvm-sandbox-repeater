package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link MapComparator}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class MapComparator implements Comparator {

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        return left instanceof Map && right instanceof Map;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        Map<?, ?> lm = (Map<?, ?>) left;
        Map<?, ?> rm = (Map<?, ?>) right;
        Set<Object> mergedKeySet = new HashSet<Object>();
        mergedKeySet.addAll(lm.keySet());
        mergedKeySet.addAll(rm.keySet());
        for (Object key : mergedKeySet) {
            if (key == null) {
                continue;
            }
            Object lValue = lm.get(key);
            Object rValue = rm.get(key);
            comparator.dispatch(lValue, rValue, comparator.declarePath(paths, key.toString()));
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }
}
