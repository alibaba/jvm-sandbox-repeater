package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.AppBO;
import com.alibaba.repeater.console.common.domain.PageResult;
import com.alibaba.repeater.console.service.impl.AppServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * {@link AppController}
 * <p>
 *
 * @author Flag
 */
@RequestMapping("/app")
@RestController
public class AppController {

    @Resource
    private AppServiceImpl appService;

    @RequestMapping("/list")
    public Object list(String keyword, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AppBO> pageResult = appService.list(keyword, page, size);
        return RepeaterResult.builder().success(true).message("operate success").data(pageResult).build();
    }

}
