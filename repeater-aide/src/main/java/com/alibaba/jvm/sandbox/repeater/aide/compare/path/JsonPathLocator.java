package com.alibaba.jvm.sandbox.repeater.aide.compare.path;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link JsonPathLocator}
 * a implement with json path location
 * <p>
 * the real location can be parsed by <a>https://github.com/json-path/JsonPath</a>
 *
 * @author zhaoyb1990
 */
public class JsonPathLocator implements PathLocator {

    private final static String ROOT_NODE = "$";

    private final static String SEPARATOR = ".";

    private final static String ARRAY_PREFIX = "[";

    private final static String ARRAY_SUFFIX = "]";

    @Override
    public String encode(List<Path> nodeNames) {
        if (nodeNames == null || nodeNames.size() == 0) {
            return ROOT_NODE;
        }
        StringBuilder builder = new StringBuilder().append(ROOT_NODE);
        for (Path node : nodeNames) {
            if (node.isNode()) {
                builder.append(SEPARATOR).append(node.value);
            } else {
                builder.append(ARRAY_PREFIX).append(node.value).append(ARRAY_SUFFIX);
            }
        }
        return builder.toString();
    }

    @Override
    public List<Path> decode(String location) {
        if (StringUtils.isEmpty(location)) {
            throw new IllegalArgumentException("location can not be null");
        }
        if (location.length() == 1 && location.equals(ROOT_NODE)) {
            return Collections.emptyList();
        }
        if (!StringUtils.startsWith(location, ROOT_NODE + SEPARATOR)) {
            throw new IllegalArgumentException("unsupported location format");
        }
        // $.result.data.user -> [$, result, data, user]
        String[] nodeNames = StringUtils.split(location, SEPARATOR);
        List<Path> paths = new ArrayList<Path>(nodeNames.length - 1);
        for (int i = 1; i < nodeNames.length; i++) {
            if (StringUtils.startsWith(nodeNames[i], ARRAY_PREFIX) && StringUtils.endsWith(nodeNames[i], ARRAY_SUFFIX)) {
                paths.add(Path.indexPath(Integer.valueOf(StringUtils.substringBetween(nodeNames[i], ARRAY_PREFIX, ARRAY_SUFFIX))));
            } else {
                paths.add(Path.nodePath(nodeNames[i]));
            }
        }
        return paths;
    }
}
