package com.alibaba.repeater.console.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * {@link Replay}
 * <p>
 *
 * @author zhaoyb1990
 */
@Entity
@Table(name = "replay")
@Getter
@Setter
public class Replay implements java.io.Serializable {

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

    private String ip;

    private String environment;

    @Column(name = "repeat_id")
    private String repeatId;

    private Integer status;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="record_id")
    private Record record;

    /* callback to fill */

    /**
     * replay traceId
     */
    private String traceId;

    private String response;

    @Column(name = "mock_invocation")
    private String mockInvocation;

    private Boolean success;

    private Long cost;

    @Column(name = "diff_result")
    private String diffResult;
}
