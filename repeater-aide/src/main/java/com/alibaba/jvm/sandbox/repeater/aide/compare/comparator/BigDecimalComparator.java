package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/8 15:14
 */
@MetaInfServices(Comparator.class)
public class BigDecimalComparator implements Comparator {

    @Override
    public int order() {
        return 9000;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }

        if (left instanceof BigDecimal || right instanceof BigDecimal) {
            return true;
        }

        return false;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        BigDecimal l = null;
        BigDecimal r = null;
        if (!(left instanceof BigDecimal)) {
            l = new BigDecimal(left.toString());
        } else {
            l = (BigDecimal) left;
        }


        if (!(right instanceof BigDecimal)) {
            r = new BigDecimal(right.toString());
        } else {
            r = (BigDecimal) right;
        }

        if (l.compareTo(r)!=0) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }
}
