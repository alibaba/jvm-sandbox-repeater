package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Difference;
import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Field;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;

/**
 * {@link ObjectComparator}
 * <p>
 * can compare complex object
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Comparator.class)
public class ObjectComparator implements Comparator {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean accept(Object left, Object right) {
        return left != null && right != null;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        // check class type is completely the same
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        if (lCs != rCs) {
            comparator.addDifference(left, right, Difference.Type.TYPE_DIFF, paths);
            return;
        }
        // dispatch field with reflect access
        innerCompare(lCs, left, right, paths, comparator);
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }

    /**
     * inner recursively dispatch field with reflect access
     *
     * @param clazz      class of two instance
     * @param left       the left object to handle
     * @param right      the right object to handle
     * @param paths      current node paths
     * @param comparator integrated comparator
     */
    private void innerCompare(Class<?> clazz, Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isSynthetic() || isTransient(field.getModifiers()) || isStatic(field.getModifiers())) {
                continue;
            }
            boolean accessible = field.isAccessible();
            // recursively dispatch with integrated comparator
            try {
                field.setAccessible(true);
                comparator.dispatch(field.get(left), field.get(right), comparator.declarePath(paths, field.getName()));
            } catch (Exception e) {
                // this may not happen
                throw new RuntimeException("illegal access with filed", e);
            } finally {
                field.setAccessible(accessible);
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        while (superClazz != null && superClazz != Object.class) {
            innerCompare(superClazz, left, right, paths, comparator);
            superClazz = superClazz.getSuperclass();
        }
    }
}
