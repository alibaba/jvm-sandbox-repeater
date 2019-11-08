package com.alibaba.jvm.sandbox.repeater.aide.compare;

import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * {@link ComparableFactory}
 * <p>
 *
 * @author zhaoyb1990
 */
public class ComparableFactory {

    private static ComparableFactory instance = new ComparableFactory();

    private volatile List<Comparator> comparators = new ArrayList<Comparator>();

    private ComparableFactory() {
        ServiceLoader<Comparator> serviceLoader = ServiceLoader.load(Comparator.class, this.getClass().getClassLoader());
        for (Comparator comparator : serviceLoader) {
            comparators.add(comparator);
            Collections.sort(comparators, new java.util.Comparator<Comparator>() {
                @Override
                public int compare(Comparator o1, Comparator o2) {
                    return o2.order() - o1.order();
                }
            });
        }
    }

    public static ComparableFactory instance() {
        return instance;
    }

    public Comparable createDefault() {
        return create(Comparator.CompareMode.DEFAULT);
    }

    public Comparable create(Comparator.CompareMode compareMode) {
        List<Comparator> comparators = new ArrayList<Comparator>();
        for (Comparator comparator : this.comparators) {
            if (comparator.support(compareMode)) {
                comparators.add(comparator);
            }
        }
        return new IntegratedComparator(comparators);
    }
}
