package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.domain.RecordBO;
import com.alibaba.repeater.console.common.domain.RecordDetailBO;
import com.alibaba.repeater.console.dal.repository.RecordRepository;
import com.alibaba.repeater.console.service.impl.RecordServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * {@link RecordController}
 * <p>
 *
 * @author Flag
 */
@RequestMapping("/record")
@RestController
public class RecordController {

    @Resource
    private RecordServiceImpl recordService;

    @Resource
    private RecordRepository recordRepository;

    @RequestMapping("/queryAppName")
    public List<String> queryAppName() {
        return recordRepository.queryAppName();
    }

    @RequestMapping("/list")
    public Object list(@RequestParam String appName, @RequestParam String keyWords, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        PageResult<RecordBO> pageResult = recordService.list(appName, keyWords, page, size);
        return pageResult;
    }

    @RequestMapping("/detail")
    public Object detail(Long id) {
        RecordDetailBO recordBO = recordService.detail(id);
        return recordBO;
    }

}
