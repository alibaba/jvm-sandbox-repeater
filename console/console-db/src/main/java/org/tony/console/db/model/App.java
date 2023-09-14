package org.tony.console.db.model;

import lombok.Data;

import java.util.Date;

@Data
public class App {

    private Long id;

    private Date gmtCreate;

    private String name;

    private String buName;

    private Integer buId;

    private Long appId;

    private String region;
}
