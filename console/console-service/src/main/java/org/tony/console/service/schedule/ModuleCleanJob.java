package org.tony.console.service.schedule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tony.console.common.domain.ModuleStatus;
import org.tony.console.common.utils.DateUtil;
import org.tony.console.db.mapper.ModuleInfoMapper;
import org.tony.console.db.model.ModuleInfo;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author peng.hu1
 * @Date 2023/1/3 13:45
 */
@Component
public class ModuleCleanJob {

    @Resource
    ModuleInfoMapper moduleInfoMapper;

    @Value("${repeater.module.max.ideal.time}")
    private int maxIdealTime= 5;

    @Scheduled(cron = "0/10 * * * * ?")
    public void cleanRecord() {

        //查询所有
        List<ModuleInfo> moduleInfos = moduleInfoMapper.queryModuleInfo(new HashMap<>());
        Date now = new Date();

        List<ModuleInfo> updateList = new LinkedList<>();
        List<ModuleInfo> removeList = new LinkedList<>();
        for (ModuleInfo moduleInfo : moduleInfos) {
            long gapH = DateUtil.getDateGapHour(moduleInfo.getGmtModified(),now);
            if (ModuleStatus.OFFLINE.name().equals(moduleInfo.getStatus())) {
                if (gapH>0) {
                    removeList.add(moduleInfo);
                    continue;
                }
            }

            if (ModuleStatus.ACTIVE.name().equals(moduleInfo.getStatus())) {
                long gapM = DateUtil.getDateGapM(moduleInfo.getGmtModified(),now);
                if (gapM > maxIdealTime) {
                    moduleInfo.setStatus(ModuleStatus.OFFLINE.name());
                    updateList.add(moduleInfo);
                    continue;
                }
            }
        }

        for (ModuleInfo moduleInfo : updateList) {
            moduleInfoMapper.updateModuleInfo(moduleInfo);
        }
        for (ModuleInfo moduleInfo : removeList) {
            moduleInfoMapper.deleteById(moduleInfo.getId());
        }

    }

}
