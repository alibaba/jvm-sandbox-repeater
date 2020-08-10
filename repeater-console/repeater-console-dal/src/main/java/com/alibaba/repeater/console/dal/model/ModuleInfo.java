package com.alibaba.repeater.console.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * {@link ModuleInfo}
 * <p>
 * 在线模块信息
 *
 * @author zhaoyb1990
 */
@Entity
@Table(name = "module_info")
@Getter
@Setter
public class ModuleInfo implements java.io.Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private ModuleConfig moduleConfig;

    private String ip;

    private String port;

    private String username;

    private String password;

    private String privateRsaFile;

    private String preCommand;

    private String status;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;
}
