package org.tony.console.service.model.app;

import lombok.Data;
import org.tony.console.db.model.App;

import java.util.LinkedList;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/3/30 15:39
 */
@Data
public class AppGroup {

    private Integer id;

    private String name;

    private List<App> appList;

    public List<App> getAppList() {
        if (appList==null) {
            appList = new LinkedList<>();
        }

        return appList;
    }
}
