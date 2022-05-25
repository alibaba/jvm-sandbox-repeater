package com.alibaba.repeater.console.start.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * {@link RegressPageController}
 * <p>
 *
 * @author zhaoyb1990
 */
@Controller
@RequestMapping("/regress")
public class RegressPageController {

    @RequestMapping("/index.htm")
    public String index() {
        return "/regress/index";
    }
}
