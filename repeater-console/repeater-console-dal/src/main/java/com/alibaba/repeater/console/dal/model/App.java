package com.alibaba.repeater.console.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link App}
 * <p>
 *
 * @author Flag
 */
@Entity
@Table(name = "app")
@Getter
@Setter
public class App implements java.io.Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    private String name;

    private String memo;

    @OneToMany(mappedBy = "app", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ModuleConfig> moduleConfigList = new ArrayList<>();


    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;
}
