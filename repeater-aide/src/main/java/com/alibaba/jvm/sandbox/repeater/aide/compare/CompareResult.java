package com.alibaba.jvm.sandbox.repeater.aide.compare;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link CompareResult}
 * <p>
 *
 * @author zhaoyb1990
 */
public class CompareResult {
    private transient Object left;
    private transient Object right;
    private List<Difference> differences = new ArrayList<Difference>();

    CompareResult(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    CompareResult(Object left, Object right, List<Difference> differences) {
        this.left = left;
        this.right = right;
        this.differences = differences;
    }

    public Object getLeft() {
        return left;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public Object getRight() {
        return right;
    }

    public void setRight(Object right) {
        this.right = right;
    }

    public List<Difference> getDifferences() {
        return differences;
    }

    public void setDifferences(List<Difference> differences) {
        this.differences = differences;
    }

    public boolean hasDifference() {
        return differences != null && differences.size() > 0;
    }
}
