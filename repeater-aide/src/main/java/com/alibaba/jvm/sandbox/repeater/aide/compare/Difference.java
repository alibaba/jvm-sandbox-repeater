package com.alibaba.jvm.sandbox.repeater.aide.compare;

import com.alibaba.jvm.sandbox.repeater.aide.compare.path.Path;

import java.util.List;

/**
 * {@link Difference}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Difference {
    private Object left;
    private Object right;
    private Type type;
    private List<Path> paths;
    private String nodeName;

    Difference(Object left, Object right, Type type, List<Path> paths, String nodeName) {
        this.left = left;
        this.right = right;
        this.type = type;
        this.paths = paths;
        this.nodeName = nodeName;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public enum Type {

        /**
         * class different
         */
        TYPE_DIFF("class different"),
        /**
         * field value different
         */
        FILED_DIFF("field value different"),
        /**
         * "compare occurred error
         */
        COMPARE_ERR("compare occurred error");

        private String reason;

        Type(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }
}
