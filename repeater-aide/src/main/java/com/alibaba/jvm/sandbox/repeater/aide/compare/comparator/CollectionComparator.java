package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.alibaba.jvm.sandbox.repeater.aide.compare.TypeUtils.isCollection;

/**
 * {@link CollectionComparator}
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class CollectionComparator implements Comparator {

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        return isCollection(lCs, rCs);
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        List<?> leftList = new ArrayList<Object>((Collection<?>) left);
        List<?> rightList = new ArrayList<Object>((Collection<?>) right);
        int max = Math.max(leftList.size(), rightList.size());
        for (int index = 0; index < max; index++) {
            Object leftObject = safeGet(leftList, index);
            Object rightObject = safeGet(rightList, index);
            comparator.dispatch(leftObject, rightObject, comparator.declarePath(paths, index));
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }

    private Object safeGet(List<?> list, int index) {
        return index >= list.size() ? null : list.get(index);
    }
}
