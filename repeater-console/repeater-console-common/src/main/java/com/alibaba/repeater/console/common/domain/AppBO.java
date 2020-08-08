package com.alibaba.repeater.console.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * {@link AppBO}
 * <p>
 *
 * @author Flag
 */
@Getter
@Setter
public class AppBO extends BaseBO {

    private Long id;

    private String name;

    private String memo;

    private Date gmtCreate;

    private Date gmtModified;


    @Override
    public String toString() {
        return super.toString();
    }
}
