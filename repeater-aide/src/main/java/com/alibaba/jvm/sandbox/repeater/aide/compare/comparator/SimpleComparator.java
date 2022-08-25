package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.List;

import static com.alibaba.jvm.sandbox.repeater.aide.compare.TypeUtils.*;

/**
 * {@link SimpleComparator}
 * <p>
 * can compare basic type use '==' or java.util/java.lang/java.math/java.time use 'equals'
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class SimpleComparator implements Comparator {

    @Override
    public int order() {
        return 10000;
    }

    @Override
    public boolean accept(final Object left, final Object right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return true;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        if (isArray(lCs, rCs) || isCollection(lCs, rCs) || isMap(lCs, rCs)) {
            return false;
        }
        // type different
        if (lCs != rCs) {
            return true;
        }
        // basic type or java.lang or java.math or java.time or java.util
        return isBasicType(lCs, rCs) || isBothJavaLang(lCs, rCs)
                || isBothJavaMath(lCs, rCs) || isBothJavaTime(lCs, rCs) || isBothJavaUtil(lCs, rCs);
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        // default use '==' to compare
        if (left == right) {
            return;
        }
        // null check
        if (left == null || right == null) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
            return;
        }

        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        if (lCs != rCs) {
            comparator.addDifference(left, right, Difference.Type.TYPE_DIFF, paths);
            return;
        }
        // basic type using == to compare
        if (isBasicType(lCs, rCs)) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
            return;
        }
        // use equals to compare
        if (!left.equals(right)) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }
}
