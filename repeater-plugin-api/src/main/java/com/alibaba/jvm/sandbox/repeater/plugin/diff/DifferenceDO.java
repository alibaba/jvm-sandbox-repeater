package com.alibaba.jvm.sandbox.repeater.plugin.diff;

/**
 * @author peng.hu1
 * @Date 2023/1/10 18:12
 */
public class DifferenceDO  extends BaseDO {

    private String actual;
    private String expect;
    private String type;
    private String nodeName;

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
