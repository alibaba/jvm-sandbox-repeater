package com.alibaba.jvm.sandbox.repeater.aide.compare.path;

import java.util.List;

/**
 * {@link PathLocator}
 * <p>
 * define a method to locate the absolute path of each node
 *
 * @author zhaoyb1990
 */
public interface PathLocator {

    /**
     * encode the node name into a specific known protocol
     *
     * @param nodeNames all names of the node traversed
     * @return a specific location
     */
    String encode(List<Path> nodeNames);

    /**
     * decode the location into a ordered node name
     *
     * @param location location produced with {@link PathLocator#encode(List)}
     * @return list of node name
     */
    List<Path> decode(String location);
}
