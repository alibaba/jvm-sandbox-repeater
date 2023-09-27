package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/8 14:21
 */
@MetaInfServices(Comparator.class)
public class CollectionWithArrayComparator implements Comparator {
    @Override
    public int order() {
        return 101;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();

        if (lCs.isArray() && Collection.class.isAssignableFrom(rCs)) {
            return true;
        }

        if (rCs.isArray() && Collection.class.isAssignableFrom(lCs)) {
            return true;
        }

        return false;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        if (Collection.class.isAssignableFrom(left.getClass())) {
            List<?> leftList = new ArrayList<Object>((Collection<?>) left);
            Object[] leftArray = leftList.toArray();
            Object[] rightArray = transfer(right);
            comparator.compare(leftArray, rightArray);
        }

        if (Collection.class.isAssignableFrom(right.getClass())) {
            List<?> rightList = new ArrayList<Object>((Collection<?>) right);
            Object[] rightArray = rightList.toArray();
            Object[] leftArray = transfer(left);
            comparator.compare(leftArray, rightArray);
        }

    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }

    private Object[] transfer(Object object) {
        if (object instanceof byte[]) {
            return ArrayUtils.toObject((byte[]) object);
        } else if (object instanceof short[]) {
            return ArrayUtils.toObject((short[]) object);
        } else if (object instanceof char[]) {
            return ArrayUtils.toObject((char[]) object);
        } else if (object instanceof int[]) {
            return ArrayUtils.toObject((int[]) object);
        } else if (object instanceof double[]) {
            return ArrayUtils.toObject((double[]) object);
        } else if (object instanceof boolean[]) {
            return ArrayUtils.toObject((boolean[]) object);
        } else if (object instanceof long[]) {
            return ArrayUtils.toObject((long[]) object);
        } else if (object instanceof float[]) {
            return ArrayUtils.toObject((float[]) object);
        } else {
            return (Object[]) object;
        }
    }
}
