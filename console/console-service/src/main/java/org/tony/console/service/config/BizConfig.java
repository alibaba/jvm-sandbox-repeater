package org.tony.console.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/10 09:53
 */
@Component
public class BizConfig {

    @Value("#{'${biz.config.superUserList}'.split(',')}")
    private List<String> superUserList;

    public List<String> getSuperUserList() {
        return superUserList;
    }

    public boolean isSuperUser(String user) {
        return superUserList.contains(user);
    }
}
