package com.alibaba.repeater.console.start.controller.page;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.params.ReplayParams;
import com.alibaba.repeater.console.service.impl.ReplayServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * {@link ReplayController}
 * <p>
 *
 * @author zhaoyb1990
 */
@Controller
@RequestMapping("/replay")
public class ReplayController {

    @Resource
    private ReplayServiceImpl replayService;

    @RequestMapping("/fetchEnv")
    @ResponseBody
    public List<String> fetchEnv(String appName) {
        return replayService.fetchEnvByAppName(appName);
    }

    @RequestMapping("/fetchHost")
    @ResponseBody
    public Map<Long, String> fetchHost(String appName, String env) {
        return replayService.fetchHost(appName, env);
    }

    @RequestMapping("/execute")
    @ResponseBody
    public RepeaterResult<String> replay(String traceId, Long moduleId, boolean isMock) {
        return replayService.replay(traceId, moduleId, isMock);
    }

    @RequestMapping("/list")
    @ResponseBody
    public Object list(Integer recordId, String repeatId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "9999") Integer size) {
        return replayService.list(recordId, repeatId, page, size);
    }



///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

    @RequestMapping("detail.htm")
    public String detail(@ModelAttribute("requestParams") ReplayParams params, Model model) {
        RepeaterResult<ReplayBO> result = replayService.query(params);
        if (!result.isSuccess()) {
            return "/error/404";
        }
        model.addAttribute("replay", result.getData());
        model.addAttribute("record", result.getData().getRecord());
        return "/replay/detail";
    }


}
