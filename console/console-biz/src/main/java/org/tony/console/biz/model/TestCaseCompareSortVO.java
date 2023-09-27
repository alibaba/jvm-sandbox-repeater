package org.tony.console.biz.model;

import lombok.Data;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/9/5 13:09
 */
@Data
public class TestCaseCompareSortVO {

    private Long id;

    private String caseId;

    private int version;

    private List<Item> configs;

    @Data
    public static class Item {

        private String key;

        private String field;
    }
}
