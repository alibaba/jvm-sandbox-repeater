package org.tony.console.biz.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2022/12/15 21:15
 */
@Data
public class TestSuitTreeVO {

    /**
     * id列表
     */
    private Long key;

    /**
     * 名称
     */
    private String title;

    /**
     * 是否展开
     */
    private boolean toggled;

    private boolean leaf;

    /**
     * 回归标签
     */
    private boolean regression;

    private List<TestSuitTreeVO> children;

    public TestSuitTreeVO() {
        this.children = new LinkedList<>();
    }

    public void addChild(TestSuitTreeVO node) {
        if (children == null) {
            children = new LinkedList<>();
        }

        children.add(node);
    }
}
