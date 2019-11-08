package com.alibaba.jvm.sandbox.repeater.aide.compare.path;

/**
 * {@link Path}
 * <p>
 *
 * @author zhaoyb1990
 */
public class Path {

    public Type type;
    public Object value;

    private Path(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static Path nodePath(String nodeName) {
        return new Path(Type.NODE, nodeName);
    }

    public static Path indexPath(int index) {
        return new Path(Type.INDEX, index);
    }

    public boolean isNode() {
        return type == Type.NODE;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * type of path
     */
    public enum Type {
        /**
         * a real value node
         */
        NODE,
        /**
         * index node
         */
        INDEX
    }
}
