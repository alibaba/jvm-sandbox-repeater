package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.Date;
import java.util.List;

/**
 * {@link LenientDateComparator}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class LenientDateComparator implements Comparator {

    @Override
    public int order() {
        return 100000;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == right) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }
        // both not null and both instance of Date
        return left instanceof Date && right instanceof Date;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        // do nothing
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return compareMode == CompareMode.LENIENT_DATES;
    }
}
