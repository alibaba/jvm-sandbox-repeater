package com.alibaba.jvm.sandbox.repeater.aide.compare.comparator;

import com.alibaba.fastjson2.JSONPath;
import com.alibaba.jvm.sandbox.repeater.aide.compare.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.util.*;

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

        //必要的时候排个序
        sort(leftList, rightList, paths, comparator);

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

    private void sort(List<?> leftList, List<?> rightList, List<Path> paths, IntegratedComparator comparator) {
        Map<String, String> sortConfig = comparator.getArraySortConfig();

        if (sortConfig==null || sortConfig.size()<=0) {
            return;
        }

        String nodePath = comparator.getNodeNameByPaths(paths);

        String sort_v_path = null;

        if (sortConfig.containsKey(nodePath)) {
            sort_v_path = sortConfig.get(nodePath);
        }

        if (sort_v_path!=null && paths.size()>0) {
            String v = String.valueOf(paths.get(paths.size()-1).value);
            sort_v_path = sortConfig.get(v);
        }

        if (StringUtils.isNotBlank(sort_v_path)) {

            String finalSort_v_path = sort_v_path;
            java.util.Comparator c = (o1, o2) -> {
                Object v1 = JSONPath.eval(o1, finalSort_v_path);
                Object v2 = JSONPath.eval(o2, finalSort_v_path);

                if (v1 == null && v2 == null) {
                    return 0;
                }

                if (v1 == null && v2 !=null) {
                    return -1;
                }

                if (v1 != null && v2 ==null) {
                    return 1;
                }

                return v1.hashCode() - v2.hashCode();
            };

            leftList.sort(c);
            rightList.sort(c);
        }

    }
}
