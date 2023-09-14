package org.tony.console.service.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.tony.console.common.enums.Status;
import org.tony.console.service.model.enums.ResourceType;
import org.tony.console.service.model.enums.Role;

import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:16
 */
@Data
public class UserResourceDTO {

    private Long id;

    private String user;

    private Long rid;

    private Role role;

    private ResourceType resourceType;

    private Status status;

    private String name;

    private Date gmtCreate;

    private Date gmtUpdate;

    /**
     * 扩展信息
     */
    private JSONObject extend;
}
