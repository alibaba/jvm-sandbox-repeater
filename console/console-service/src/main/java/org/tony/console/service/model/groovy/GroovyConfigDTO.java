package org.tony.console.service.model.groovy;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.GroovyType;
import lombok.Data;
import org.tony.console.common.enums.Status;
import org.tony.console.common.enums.Env;

import java.util.Date;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/28 19:26
 */
@Data
public class GroovyConfigDTO {

    private Long id;

    private String appName;

    private GroovyType groovyType;

    private Status status;

    private String user;

    private int version;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String content;

    private String name;

    /**
     * 适用环境
     */
    private List<Env> envList;


    public boolean isValid() {
        return this.status.equals(Status.VALID);
    }
}
