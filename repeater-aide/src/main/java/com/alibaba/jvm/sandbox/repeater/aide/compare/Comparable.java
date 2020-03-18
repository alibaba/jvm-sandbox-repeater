package com.alibaba.jvm.sandbox.repeater.aide.compare;

/**
 * {@link Comparable}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Comparable {

    /**
     * compare to object
     *
     * @param left  left object to be compare
     * @param right right object to be compare
     * @return compare result
     */
    CompareResult compare(Object left, Object right);
}
