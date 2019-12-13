package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.TypeUtils;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * {@link ArrayComparator}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class ArrayComparator implements Comparator {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        return TypeUtils.isArray(lCs, rCs);
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        Object[] leftArray = transfer(left);
        Object[] rightArray = transfer(right);
        int max = Math.max(leftArray.length, rightArray.length);
        for (int i = 0; i < max; i++) {
            Object leftObject = safeGet(leftArray, i);
            Object rightObject = safeGet(rightArray, i);
            comparator.dispatch(leftObject, rightObject, comparator.declarePath(paths, i));
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }

    private Object safeGet(Object[] array, int index) {
        return index >= array.length ? null : array[index];
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
