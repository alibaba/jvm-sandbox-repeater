package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/9/5 10:24
 */
@Data
public class TestCaseConfig {

    private Long id;

    private String caseId;

    private String config;

    private int type;

    private Date gmtCreate;

    private Date gmtUpdate;

    private int version;
}
