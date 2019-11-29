package com.alibaba.jvm.sandbox.repeater.aide.compare.cycle;

import com.alibaba.jvm.sandbox.repeater.aide.compare.TypeUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link CycleReferenceDetector}
 * <p>
 *
 * @author zhaoyb1990
 */
public class CycleReferenceDetector {

    private List<Node> nodes = new LinkedList<Node>();

    public void detect(Object instance, String nodeName) throws CycleReferenceException {

        if (instance == null) {
            return;
        }

        // ignore basic type and well know detected
        Class<?> clazz = instance.getClass();
        if (clazz.isPrimitive() || TypeUtils.isJavaWellKnown(clazz)) {
            return;
        }

        Node node = findNode(instance);
        if (node != null) {
            throw new CycleReferenceException(node.nodeName + " detected cycle reference, current  nodeName = " + nodeName);
        }
        nodes.add(new Node(instance, nodeName));
    }

    private Node findNode(Object instance) {
        for (Node node : nodes) {
            // is same instance
            if (node.instance == instance) {
                return node;
            }
        }
        return null;
    }

    public void clear() {
        nodes.clear();
    }

    class Node {

        Object instance;
        String nodeName;

        Node(Object instance, String nodeName) {
            this.instance = instance;
            this.nodeName = nodeName;
        }
    }
}
