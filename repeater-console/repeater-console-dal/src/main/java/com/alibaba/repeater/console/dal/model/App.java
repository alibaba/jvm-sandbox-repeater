package com.alibaba.repeater.console.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;
}
