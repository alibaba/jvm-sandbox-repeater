package com.alibaba.repeater.console.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * {@link ModuleConfig}
 * <p>
 *
 * @author zhaoyb1990
 */
@Entity
@Table(name = "module_config")
@Getter
@Setter
public class ModuleConfig implements java.io.Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "app_name")
    private String appName;

    private String environment;

    private String config;
}
