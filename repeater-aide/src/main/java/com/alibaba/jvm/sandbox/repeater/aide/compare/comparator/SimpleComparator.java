package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        if (left instanceof Map && right instanceof Map) {
            return false;
        }

        if (left instanceof BigDecimal || right instanceof BigDecimal) {
            return false;
        }

        if ((Collection.class.isAssignableFrom(lCs) && rCs.isArray()) || (Collection.class.isAssignableFrom(rCs) && lCs.isArray()))
        {
            return false;
        }

        // type different
        if (lCs != rCs) {
            return true;
        }
        if (isArray(lCs, rCs) || isCollection(lCs, rCs) || isMap(lCs, rCs)) {
            return false;
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

            if (isDateCompare(lCs, rCs)) {
                if (lCs.equals(Date.class)) {
                    if (!right.equals(((Date)left).getTime())) {
                        comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
                    }
                } else {
                    if (!left.equals(((Date)right).getTime())) {
                        comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
                    }
                }
                return;
            }

            //如果都是简单类型，直接转换成String进行比较, 因为可能是序列化问题出现类型不一致
            if (isSimpleClass(lCs) && isSimpleClass(rCs)) {
                if (!left.toString().equals(right.toString())) {
                    comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
                }

            } else {
                comparator.addDifference(left, right, Difference.Type.TYPE_DIFF, paths);
            }

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

    private boolean isSimpleClass(Class<?> cls) {
        if (cls.equals(Long.class)) {
            return true;
        }

        if (cls.equals(Integer.class)) {
            return true;
        }

        if (cls.equals(Short.class)) {
            return true;
        }

        if (cls.equals(Byte.class)) {
            return true;
        }

        return false;
    }

    private boolean isDateCompare(Class<?> cls,  Class<?> cls2) {

        if (cls.equals(Date.class) && cls2.equals(Long.class)) {
            return true;
        }

        if (cls.equals(Long.class) && cls2.equals(Date.class)) {
            return true;
        }

        return false;
    }
}
