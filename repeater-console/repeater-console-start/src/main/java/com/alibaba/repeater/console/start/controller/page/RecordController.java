package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.common.domain.RecordBO;
import com.alibaba.repeater.console.common.domain.RecordDetailBO;
import com.alibaba.repeater.console.service.impl.RecordServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @RequestMapping("/list")
    public Object list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        PageResult<RecordBO> pageResult = recordService.list(page, size);
        return pageResult;
    }

    @RequestMapping("/detail")
    public Object detail(Long id) {
        RecordDetailBO recordBO = recordService.detail(id);
        return recordBO;
    }

}
