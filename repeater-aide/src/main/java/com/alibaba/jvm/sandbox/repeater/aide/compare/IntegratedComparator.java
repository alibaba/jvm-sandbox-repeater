package com.alibaba.jvm.sandbox.repeater.aide.compare;

import com.alibaba.jvm.sandbox.repeater.aide.compare.comparator.Comparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.cycle.CycleReferenceDetector;
import com.alibaba.jvm.sandbox.repeater.aide.compare.cycle.CycleReferenceException;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.JsonPathLocator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.PathLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IntegratedComparator}
 * <p>
 *
 * @author zhaoyb1990
 */
public class IntegratedComparator implements Comparable {

    private final List<Comparator> comparators;
    private final PathLocator pathLocator = new JsonPathLocator();
    private final CycleReferenceDetector leftDetector = new CycleReferenceDetector();
    private final CycleReferenceDetector rightDetector = new CycleReferenceDetector();
    private List<Difference> differences = new ArrayList<Difference>();

    IntegratedComparator(List<Comparator> comparators) {
        if (comparators == null || comparators.size() == 0) {
            throw new RuntimeException("comparators can not be null or empty");
        }
        this.comparators = comparators;
    }

    public void dispatch(Object left, Object right, List<Path> paths) {
        // cycle reference detect
        try {
            String nodeName = pathLocator.encode(paths);
            leftDetector.detect(left, nodeName);
            rightDetector.detect(right, nodeName);
        } catch (CycleReferenceException e) {
           // LogUtil.error("error occurred when dispatch compare task", e);
            return;
        }
        //  do compare
        for (Comparator comparator : comparators) {
            if (comparator.accept(left, right)) {
                comparator.compare(left, right, paths, this);
                break;
            }
        }
    }

    @Override
    public CompareResult compare(Object left, Object right) {
        // try clear last compare result
        tryClear();
        // dispatch compare task
        List<Path> paths = new ArrayList<Path>(0);
        try {
            dispatch(left, right, paths);
        } catch (Exception e) {
            // LogUtil.error("error occurred when dispatch compare task", e);
            addDifference(left, right, Difference.Type.COMPARE_ERR, paths);
        }
        return new CompareResult(left, right, differences);
    }

    private void tryClear() {
        differences = new ArrayList<Difference>();
        leftDetector.clear();
        rightDetector.clear();
    }

    public void addDifference(Object left, Object right, Difference.Type type, List<Path> paths) {
        differences.add(new Difference(left, right, type, paths, pathLocator.encode(paths)));
    }

    public List<Path> declarePath(List<Path> paths, int index) {
        List<Path> target = new ArrayList<Path>(paths);
        target.add(Path.indexPath(index));
        return target;
    }

    public List<Path> declarePath(List<Path> paths, String key) {
        List<Path> target = new ArrayList<Path>(paths);
        target.add(Path.nodePath(key));
        return target;
    }
}
