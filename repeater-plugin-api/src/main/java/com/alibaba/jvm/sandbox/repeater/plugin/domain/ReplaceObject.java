package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * 替换对象，用于mock参数与回放参数不同时替换。例子：生产录制了topic是xx-prd的mq消息，回放时选的是xx-test的测试环境
 *
 * @author zzm
 * @date 2021年6月7日
 */
public class ReplaceObject {
    private Object source;// 源对象(如xx-prd)，如果不填则代表通配
    private Object target;// 目标对象(如xx-test)

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
