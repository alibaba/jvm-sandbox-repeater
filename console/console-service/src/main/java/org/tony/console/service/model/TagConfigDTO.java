package org.tony.console.service.model;

import lombok.Data;
import org.tony.console.service.model.enums.TagConfigScope;

/**
 * @author peng.hu1
 * @Date 2022/12/18 13:27
 */
@Data
public class TagConfigDTO {

    private Long id;

    private String appName;

    private String name;

    private String nickName;

    private TagConfigScope scope;

    private String identity;

    private String jsonpath;

}
