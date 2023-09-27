package org.tony.console.service.model.app;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/4/2 10:19
 */
@Data
public class AppDTO {

    private Long id;

    private Date gmtCreate;

    private String name;

    private String buName;

    private Integer buId;

    private Long appId;

    private List<String> admins;

    /**
     * 区域，主要区分国内、国外
     */
    private Region region;
}
